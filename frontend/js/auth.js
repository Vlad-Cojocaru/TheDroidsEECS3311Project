function renderLoginPage() {
    document.getElementById('page-login').innerHTML = `
        <div class="auth-container">
            <h2>Service Booking &amp; Consulting Platform</h2>
            <p class="subtitle">Team: The Droids | EECS 3311</p>
            <div id="auth-form">
                <div class="tabs">
                    <button class="tab active" onclick="showLoginTab(this)">Login</button>
                    <button class="tab" onclick="showRegisterTab(this)">Register</button>
                </div>
                <div id="login-form">
                    <input type="email" id="login-email" placeholder="Email">
                    <input type="password" id="login-password" placeholder="Password">
                    <button class="btn-primary" onclick="doLogin()">Login</button>
                    <div id="login-error" class="error"></div>
                    <div class="hint">
                        Demo accounts: admin@thedroids.com / admin123 |
                        dave@example.com / pass123 |
                        alice@thedroids.com / pass123
                    </div>
                </div>
                <div id="register-form" style="display:none;">
                    <input type="text" id="reg-name" placeholder="Full Name">
                    <input type="email" id="reg-email" placeholder="Email">
                    <input type="password" id="reg-password" placeholder="Password">
                    <select id="reg-role">
                        <option value="CLIENT">Client</option>
                        <option value="CONSULTANT">Consultant</option>
                    </select>
                    <div id="consultant-fields" style="display:none;">
                        <input type="text" id="reg-specialization" placeholder="Specialization">
                        <input type="number" id="reg-hourly-rate" placeholder="Hourly Rate ($)" step="10" value="100">
                    </div>
                    <button class="btn-primary" onclick="doRegister()">Register</button>
                    <div id="register-error" class="error"></div>
                </div>
            </div>
        </div>`;

    document.getElementById('reg-role').addEventListener('change', function() {
        document.getElementById('consultant-fields').style.display =
            this.value === 'CONSULTANT' ? 'block' : 'none';
    });
}

function showLoginTab(btn) {
    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
    btn.classList.add('active');
    document.getElementById('login-form').style.display = 'block';
    document.getElementById('register-form').style.display = 'none';
}

function showRegisterTab(btn) {
    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
    btn.classList.add('active');
    document.getElementById('login-form').style.display = 'none';
    document.getElementById('register-form').style.display = 'block';
}

async function doLogin() {
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    const result = await apiPost('/auth/login', { email, password });
    if (result.error) {
        document.getElementById('login-error').textContent = result.error;
    } else {
        setCurrentUser(result);
    }
}

async function doRegister() {
    const body = {
        name: document.getElementById('reg-name').value,
        email: document.getElementById('reg-email').value,
        password: document.getElementById('reg-password').value,
        role: document.getElementById('reg-role').value
    };
    if (body.role === 'CONSULTANT') {
        body.specialization = document.getElementById('reg-specialization').value;
        body.hourlyRate = document.getElementById('reg-hourly-rate').value;
    }
    const result = await apiPost('/auth/register', body);
    if (result.error) {
        document.getElementById('register-error').textContent = result.error;
    } else {
        if (result.role === 'CONSULTANT' && !result.approved) {
            document.getElementById('register-error').textContent =
                'Registered! Your account is pending admin approval.';
            document.getElementById('register-error').style.color = '#2563eb';
        } else {
            setCurrentUser(result);
        }
    }
}
