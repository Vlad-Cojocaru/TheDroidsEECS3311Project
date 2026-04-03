// --- UC9: Pending Booking Requests ---
async function loadConsultantPending() {
    const bookings = await apiGet('/bookings/consultant/' + currentUser.id + '/pending');
    let html = '<div class="section"><h3>Pending Booking Requests</h3>';
    if (!bookings.length) {
        html += '<p>No pending requests.</p>';
    } else {
        html += '<table><tr><th>ID</th><th>Client</th><th>Service</th><th>Created</th><th>Actions</th></tr>';
        for (const b of bookings) {
            html += `<tr>
                <td title="${b.id}">${b.id.substring(0,8)}...</td>
                <td>${b.clientId.substring(0,8)}...</td>
                <td>${b.serviceId.substring(0,8)}...</td>
                <td>${b.createdAt ? b.createdAt.substring(0,10) : ''}</td>
                <td>
                    <button class="btn-sm btn-accept" onclick="acceptBooking('${b.id}')">Accept</button>
                    <button class="btn-sm btn-reject" onclick="rejectBooking('${b.id}')">Reject</button>
                </td>
            </tr>`;
        }
        html += '</table>';
    }
    html += '</div>';
    document.getElementById('page-consultant').innerHTML = html;
}

async function acceptBooking(id) {
    const result = await apiPut('/bookings/' + id + '/accept');
    alert(result.message || result.error);
    loadConsultantPending();
}

async function rejectBooking(id) {
    if (!confirm('Reject this booking?')) return;
    const result = await apiPut('/bookings/' + id + '/reject');
    alert(result.message || result.error);
    loadConsultantPending();
}

// --- UC10: Paid Bookings (mark complete) ---
async function loadConsultantPaid() {
    const bookings = await apiGet('/bookings/consultant/' + currentUser.id + '/paid');
    let html = '<div class="section"><h3>Paid Bookings (Ready to Complete)</h3>';
    if (!bookings.length) {
        html += '<p>No paid bookings awaiting completion.</p>';
    } else {
        html += '<table><tr><th>ID</th><th>Client</th><th>Service</th><th>Action</th></tr>';
        for (const b of bookings) {
            html += `<tr>
                <td title="${b.id}">${b.id.substring(0,8)}...</td>
                <td>${b.clientId.substring(0,8)}...</td>
                <td>${b.serviceId.substring(0,8)}...</td>
                <td><button class="btn-sm btn-complete" onclick="completeBooking('${b.id}')">Complete</button></td>
            </tr>`;
        }
        html += '</table>';
    }
    html += '</div>';
    document.getElementById('page-consultant').innerHTML = html;
}

async function completeBooking(id) {
    const result = await apiPut('/bookings/' + id + '/complete');
    alert(result.message || result.error);
    loadConsultantPaid();
}

// --- UC8: Manage Availability ---
async function loadConsultantAvailability() {
    const slots = await apiGet('/availability/' + currentUser.id);
    let html = '<div class="section"><h3>My Available Time Slots</h3>';
    html += `<div class="inline-form">
        <h4>Add Time Slot</h4>
        <div class="form-row">
            <input type="date" id="slot-date" min="${new Date().toISOString().split('T')[0]}">
            <input type="time" id="slot-start" value="09:00">
            <input type="time" id="slot-end" value="10:00">
        </div>
        <button class="btn-primary" onclick="addTimeSlot()">Add Slot</button>
        <div id="slot-error" class="error"></div>
    </div>`;

    if (slots.length) {
        html += '<table><tr><th>Date</th><th>Time</th><th>Action</th></tr>';
        for (const s of slots) {
            html += `<tr>
                <td>${s.date}</td>
                <td>${s.startTime} - ${s.endTime}</td>
                <td><button class="btn-sm btn-remove" onclick="removeSlot('${s.id}')">Remove</button></td>
            </tr>`;
        }
        html += '</table>';
    } else {
        html += '<p>No available slots. Add some above.</p>';
    }
    html += '</div>';
    document.getElementById('page-consultant').innerHTML = html;
}

async function addTimeSlot() {
    const result = await apiPost('/availability', {
        consultantId: currentUser.id,
        date: document.getElementById('slot-date').value,
        startTime: document.getElementById('slot-start').value,
        endTime: document.getElementById('slot-end').value
    });
    if (result.error) {
        document.getElementById('slot-error').textContent = result.error;
    } else {
        loadConsultantAvailability();
    }
}

async function removeSlot(id) {
    if (!confirm('Remove this time slot?')) return;
    await apiDelete('/availability/' + id);
    loadConsultantAvailability();
}

// --- Full Schedule ---
async function loadConsultantSchedule() {
    const bookings = await apiGet('/bookings/consultant/' + currentUser.id);
    let html = '<div class="section"><h3>Full Booking Schedule</h3>';
    if (!bookings.length) {
        html += '<p>No bookings.</p>';
    } else {
        html += '<table><tr><th>ID</th><th>Client</th><th>Status</th><th>Created</th></tr>';
        for (const b of bookings) {
            html += `<tr>
                <td title="${b.id}">${b.id.substring(0,8)}...</td>
                <td>${b.clientId.substring(0,8)}...</td>
                <td><span class="status status-${b.status}">${b.status}</span></td>
                <td>${b.createdAt ? b.createdAt.substring(0,10) : ''}</td>
            </tr>`;
        }
        html += '</table>';
    }
    html += '</div>';
    document.getElementById('page-consultant').innerHTML = html;
}
