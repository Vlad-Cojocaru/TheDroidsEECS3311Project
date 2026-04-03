let currentUser = null;

function initRouter() {
    const saved = sessionStorage.getItem('currentUser');
    if (saved) {
        currentUser = JSON.parse(saved);
        showDashboard();
    } else {
        showLogin();
    }
}

function setCurrentUser(user) {
    currentUser = user;
    sessionStorage.setItem('currentUser', JSON.stringify(user));
    showDashboard();
}

function logout() {
    currentUser = null;
    sessionStorage.removeItem('currentUser');
    showLogin();
}

function showLogin() {
    document.getElementById('app-header').style.display = 'none';
    document.getElementById('chatbot-widget').style.display = 'none';
    hideAllPages();
    document.getElementById('page-login').style.display = 'block';
    renderLoginPage();
}

function showDashboard() {
    document.getElementById('app-header').style.display = 'block';
    document.getElementById('user-display').textContent =
        currentUser.name + ' (' + currentUser.role + ')';
    hideAllPages();

    const role = currentUser.role;
    if (role === 'CLIENT') {
        document.getElementById('page-client').style.display = 'block';
        document.getElementById('chatbot-widget').style.display = 'block';
        buildClientNav();
        loadClientServices();
    } else if (role === 'CONSULTANT') {
        document.getElementById('page-consultant').style.display = 'block';
        document.getElementById('chatbot-widget').style.display = 'none';
        buildConsultantNav();
        loadConsultantPending();
    } else if (role === 'ADMIN') {
        document.getElementById('page-admin').style.display = 'block';
        document.getElementById('chatbot-widget').style.display = 'none';
        buildAdminNav();
        loadPendingConsultants();
    }
}

function hideAllPages() {
    document.getElementById('page-login').style.display = 'none';
    document.getElementById('page-client').style.display = 'none';
    document.getElementById('page-consultant').style.display = 'none';
    document.getElementById('page-admin').style.display = 'none';
}

function buildClientNav() {
    document.getElementById('main-nav').innerHTML =
        '<button onclick="loadClientServices()">Browse Services</button>' +
        '<button onclick="loadClientBookings()">My Bookings</button>' +
        '<button onclick="loadClientPaymentMethods()">Payment Methods</button>' +
        '<button onclick="loadClientPaymentHistory()">Payment History</button>';
}

function buildConsultantNav() {
    document.getElementById('main-nav').innerHTML =
        '<button onclick="loadConsultantPending()">Pending Requests</button>' +
        '<button onclick="loadConsultantPaid()">Paid Bookings</button>' +
        '<button onclick="loadConsultantAvailability()">My Availability</button>' +
        '<button onclick="loadConsultantSchedule()">Full Schedule</button>';
}

function buildAdminNav() {
    document.getElementById('main-nav').innerHTML =
        '<button onclick="loadPendingConsultants()">Pending Consultants</button>' +
        '<button onclick="loadAdminPolicy()">System Policy</button>';
}
