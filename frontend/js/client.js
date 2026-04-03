// --- UC1: Browse Services ---
async function loadClientServices() {
    const services = await apiGet('/services');
    let html = '<div class="section"><h3>Available Consulting Services</h3>';
    if (!services.length) {
        html += '<p>No services available.</p>';
    } else {
        html += '<table><tr><th>Name</th><th>Type</th><th>Duration</th><th>Price</th><th>Action</th></tr>';
        for (const s of services) {
            html += `<tr>
                <td>${s.name}<br><small style="color:#6b7280">${s.description}</small></td>
                <td>${s.type}</td>
                <td>${s.durationMinutes} min</td>
                <td>$${s.basePrice.toFixed(2)}</td>
                <td><button class="btn-sm btn-pay" onclick="startBooking('${s.id}','${s.consultantId}')">Book</button></td>
            </tr>`;
        }
        html += '</table>';
    }
    html += '</div><div id="booking-form-area"></div>';
    document.getElementById('page-client').innerHTML = html;
}

// --- UC2: Request Booking ---
async function startBooking(serviceId, consultantId) {
    const slots = await apiGet('/availability/' + consultantId);
    let html = '<div class="inline-form"><h4>Select a Time Slot</h4>';
    if (!slots.length) {
        html += '<p>No available time slots for this consultant.</p></div>';
    } else {
        html += '<table><tr><th>Date</th><th>Time</th><th>Action</th></tr>';
        for (const sl of slots) {
            html += `<tr>
                <td>${sl.date}</td>
                <td>${sl.startTime} - ${sl.endTime}</td>
                <td><button class="btn-sm btn-accept" onclick="confirmBooking('${serviceId}','${consultantId}','${sl.id}')">Select</button></td>
            </tr>`;
        }
        html += '</table></div>';
    }
    document.getElementById('booking-form-area').innerHTML = html;
}

async function confirmBooking(serviceId, consultantId, timeSlotId) {
    const result = await apiPost('/bookings', {
        clientId: currentUser.id,
        consultantId, serviceId, timeSlotId
    });
    if (result.error) {
        alert(result.error);
    } else {
        alert('Booking requested! ID: ' + result.id);
        loadClientBookings();
    }
}

// --- UC3 + UC4: My Bookings ---
async function loadClientBookings() {
    const bookings = await apiGet('/bookings/client/' + currentUser.id);
    let html = '<div class="section"><h3>My Bookings</h3>';
    if (!bookings.length) {
        html += '<p>No bookings yet.</p>';
    } else {
        html += '<table><tr><th>ID</th><th>Status</th><th>Service</th><th>Created</th><th>Actions</th></tr>';
        for (const b of bookings) {
            const canCancel = ['REQUESTED','CONFIRMED','PENDING_PAYMENT'].includes(b.status);
            const canPay = b.status === 'PENDING_PAYMENT';
            html += `<tr>
                <td title="${b.id}">${b.id.substring(0,8)}...</td>
                <td><span class="status status-${b.status}">${b.status}</span></td>
                <td>${b.serviceId.substring(0,8)}...</td>
                <td>${b.createdAt ? b.createdAt.substring(0,10) : ''}</td>
                <td>
                    ${canCancel ? `<button class="btn-sm btn-cancel" onclick="cancelBooking('${b.id}')">Cancel</button>` : ''}
                    ${canPay ? `<button class="btn-sm btn-pay" onclick="showPayForm('${b.id}')">Pay</button>` : ''}
                </td>
            </tr>`;
        }
        html += '</table>';
    }
    html += '</div><div id="pay-form-area"></div>';
    document.getElementById('page-client').innerHTML = html;
}

async function cancelBooking(bookingId) {
    if (!confirm('Cancel this booking?')) return;
    const result = await apiPut('/bookings/' + bookingId + '/cancel');
    alert(result.message || result.error);
    loadClientBookings();
}

// --- UC5: Process Payment ---
async function showPayForm(bookingId) {
    const methods = await apiGet('/payment-methods/' + currentUser.id);
    let html = '<div class="inline-form"><h4>Process Payment</h4>';
    if (!methods.length) {
        html += '<p>No payment methods. <button class="btn-sm btn-add" onclick="loadClientPaymentMethods()">Add One</button></p></div>';
    } else {
        html += '<select id="pay-method-select">';
        for (const m of methods) {
            html += `<option value="${m.id}">${m.type} (${m.id.substring(0,8)}...)</option>`;
        }
        html += '</select>';
        html += `<button class="btn-primary" style="margin-top:0.5rem" onclick="doPayment('${bookingId}')">Process Payment</button>`;
        html += '</div>';
    }
    document.getElementById('pay-form-area').innerHTML = html;
}

async function doPayment(bookingId) {
    const methodId = document.getElementById('pay-method-select').value;
    const result = await apiPost('/payments', { bookingId, paymentMethodId: methodId });
    if (result.error) {
        alert(result.error);
    } else {
        alert('Payment successful! Transaction: ' + result.transactionId);
        loadClientBookings();
    }
}

// --- UC6: Manage Payment Methods ---
async function loadClientPaymentMethods() {
    const methods = await apiGet('/payment-methods/' + currentUser.id);
    let html = '<div class="section"><h3>My Payment Methods</h3>';
    html += `<div class="inline-form">
        <h4>Add Payment Method</h4>
        <div class="form-row">
            <select id="pm-type" onchange="updatePmFields()">
                <option value="CREDIT_CARD">Credit Card</option>
                <option value="DEBIT_CARD">Debit Card</option>
                <option value="PAYPAL">PayPal</option>
                <option value="BANK_TRANSFER">Bank Transfer</option>
            </select>
        </div>
        <div id="pm-fields"></div>
        <button class="btn-primary" onclick="addPaymentMethod()">Add Method</button>
        <div id="pm-error" class="error"></div>
    </div>`;

    if (methods.length) {
        html += '<table><tr><th>Type</th><th>ID</th><th>Action</th></tr>';
        for (const m of methods) {
            html += `<tr>
                <td>${m.type}</td>
                <td>${m.id.substring(0,8)}...</td>
                <td><button class="btn-sm btn-remove" onclick="removePaymentMethod('${m.id}')">Remove</button></td>
            </tr>`;
        }
        html += '</table>';
    } else {
        html += '<p>No payment methods added yet.</p>';
    }
    html += '</div>';
    document.getElementById('page-client').innerHTML = html;
    updatePmFields();
}

function updatePmFields() {
    const type = document.getElementById('pm-type').value;
    let fields = '';
    if (type === 'CREDIT_CARD' || type === 'DEBIT_CARD') {
        fields = `
            <input type="text" id="pm-card" placeholder="Card Number (16 digits)" maxlength="16">
            <div class="form-row">
                <input type="text" id="pm-expiry" placeholder="Expiry (MM/YY)">
                <input type="text" id="pm-cvv" placeholder="CVV (3-4 digits)" maxlength="4">
            </div>`;
    } else if (type === 'PAYPAL') {
        fields = '<input type="email" id="pm-paypal-email" placeholder="PayPal Email">';
    } else {
        fields = `
            <input type="text" id="pm-account" placeholder="Account Number">
            <input type="text" id="pm-routing" placeholder="Routing Number">`;
    }
    document.getElementById('pm-fields').innerHTML = fields;
}

async function addPaymentMethod() {
    const type = document.getElementById('pm-type').value;
    let details = {};
    if (type === 'CREDIT_CARD' || type === 'DEBIT_CARD') {
        details = {
            cardNumber: document.getElementById('pm-card').value,
            expiryDate: document.getElementById('pm-expiry').value,
            cvv: document.getElementById('pm-cvv').value
        };
    } else if (type === 'PAYPAL') {
        details = { email: document.getElementById('pm-paypal-email').value };
    } else {
        details = {
            accountNumber: document.getElementById('pm-account').value,
            routingNumber: document.getElementById('pm-routing').value
        };
    }
    const result = await apiPost('/payment-methods', {
        clientId: currentUser.id, type, details
    });
    if (result.error) {
        document.getElementById('pm-error').textContent = result.error;
    } else {
        loadClientPaymentMethods();
    }
}

async function removePaymentMethod(id) {
    if (!confirm('Remove this payment method?')) return;
    await apiDelete('/payment-methods/' + id);
    loadClientPaymentMethods();
}

// --- UC7: Payment History ---
async function loadClientPaymentHistory() {
    const payments = await apiGet('/payments/history/' + currentUser.id);
    let html = '<div class="section"><h3>Payment History</h3>';
    if (!payments.length) {
        html += '<p>No payments yet.</p>';
    } else {
        html += '<table><tr><th>Transaction</th><th>Amount</th><th>Status</th><th>Date</th></tr>';
        for (const p of payments) {
            html += `<tr>
                <td>${p.transactionId || 'N/A'}</td>
                <td>$${p.amount.toFixed(2)}</td>
                <td>${p.status}</td>
                <td>${p.timestamp ? p.timestamp.substring(0,10) : ''}</td>
            </tr>`;
        }
        html += '</table>';
    }
    html += '</div>';
    document.getElementById('page-client').innerHTML = html;
}
