// --- UC11: Approve Consultant Registration ---
async function loadPendingConsultants() {
    const consultants = await apiGet('/admin/pending-consultants');
    let html = '<div class="section"><h3>Pending Consultant Registrations</h3>';
    if (!consultants.length) {
        html += '<p>No pending consultants.</p>';
    } else {
        html += '<table><tr><th>Name</th><th>Email</th><th>Specialization</th><th>Rate</th><th>Actions</th></tr>';
        for (const c of consultants) {
            html += `<tr>
                <td>${c.name}</td>
                <td>${c.email}</td>
                <td>${c.specialization || 'N/A'}</td>
                <td>$${(c.hourlyRate || 0).toFixed(2)}/hr</td>
                <td>
                    <button class="btn-sm btn-accept" onclick="approveConsultant('${c.id}')">Approve</button>
                    <button class="btn-sm btn-reject" onclick="rejectConsultant('${c.id}')">Reject</button>
                </td>
            </tr>`;
        }
        html += '</table>';
    }
    html += '</div>';
    document.getElementById('page-admin').innerHTML = html;
}

async function approveConsultant(id) {
    const result = await apiPut('/admin/consultants/' + id + '/approve');
    alert(result.message || result.error);
    loadPendingConsultants();
}

async function rejectConsultant(id) {
    if (!confirm('Reject this consultant?')) return;
    const result = await apiPut('/admin/consultants/' + id + '/reject');
    alert(result.message || result.error);
    loadPendingConsultants();
}

// --- UC12: System Policy ---
async function loadAdminPolicy() {
    const policy = await apiGet('/admin/policy');
    let html = `<div class="section"><h3>System Policies</h3>
        <div class="inline-form">
            <label>Cancellation Window (hours):</label>
            <input type="number" id="pol-window" value="${policy.cancellationWindowHours}">
            <label>Cancellation Fee (%):</label>
            <input type="number" id="pol-fee" value="${policy.cancellationFeePercent}" step="1">
            <label>Refund (%):</label>
            <input type="number" id="pol-refund" value="${policy.refundPercent}" step="1">
            <label>
                <input type="checkbox" id="pol-notif" ${policy.notificationsEnabled ? 'checked' : ''}> Notifications Enabled
            </label>
            <button class="btn-primary" style="margin-top:0.75rem" onclick="savePolicy()">Save Policy</button>
            <div id="policy-msg" class="msg-info"></div>
        </div>
    </div>`;
    document.getElementById('page-admin').innerHTML = html;
}

async function savePolicy() {
    const policy = {
        cancellationWindowHours: parseInt(document.getElementById('pol-window').value),
        cancellationFeePercent: parseFloat(document.getElementById('pol-fee').value),
        refundPercent: parseFloat(document.getElementById('pol-refund').value),
        notificationsEnabled: document.getElementById('pol-notif').checked
    };
    const result = await apiPut('/admin/policy', policy);
    document.getElementById('policy-msg').textContent = result.message || result.error || 'Saved.';
}
