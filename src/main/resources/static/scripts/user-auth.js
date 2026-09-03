let loggedInUserId = localStorage.getItem('rentalx_userId') || "";

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    const savedToken = localStorage.getItem('rentalx_token');
    const savedUser = localStorage.getItem('rentalx_userId');
    if (savedToken && savedUser && document.getElementById('profileSection')) {
        loggedInUserId = savedUser;
        loadProfile();
    }
});

function getAuthHeaders() {
    const token = localStorage.getItem('rentalx_token');
    const headers = { 'Content-Type': 'application/json' };
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
}

if (document.getElementById('registerForm')) {
    document.getElementById('registerForm').addEventListener('submit', function (e) {
        e.preventDefault();

        const user = {
            userId: document.getElementById('regId').value.trim(),
            name: document.getElementById('regName').value.trim(),
            email: document.getElementById('regEmail').value.trim(),
            phone: document.getElementById('regPhone').value.trim(),
            password: document.getElementById('regPassword').value
        };

        // Try modern auth register endpoint
        fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(user)
        })
        .then(async res => {
            const data = await res.json().catch(() => null);
            if (res.ok) {
                alert("Account created successfully! You can now log in.");
                document.getElementById('registerForm').reset();
            } else {
                alert(data && data.message ? data.message : "Registration failed.");
            }
        })
        .catch(() => {
            // Fallback to legacy endpoint
            fetch('/user/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(user)
            }).then(() => {
                alert("Registered successfully!");
                document.getElementById('registerForm').reset();
            });
        });
    });
}

if (document.getElementById('loginForm')) {
    document.getElementById('loginForm').addEventListener('submit', function (e) {
        e.preventDefault();

        const userId = document.getElementById('loginId').value.trim();
        const password = document.getElementById('loginPassword').value;

        fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: userId, password: password })
        })
        .then(async res => {
            const result = await res.json().catch(() => null);
            if (res.ok && result && result.data) {
                const authData = result.data;
                localStorage.setItem('rentalx_token', authData.token);
                localStorage.setItem('rentalx_userId', authData.userId);
                localStorage.setItem('rentalx_name', authData.name);
                localStorage.setItem('rentalx_role', authData.role);
                loggedInUserId = authData.userId;

                alert(`Welcome back, ${authData.name}!`);

                if (document.getElementById('profileSection')) {
                    loadProfile();
                } else {
                    window.location.href = 'HomePage.html';
                }
            } else {
                // Fallback attempt
                loginLegacy(userId, password);
            }
        })
        .catch(() => loginLegacy(userId, password));
    });
}

function loginLegacy(userId, password) {
    fetch(`/user/login?userId=${encodeURIComponent(userId)}&password=${encodeURIComponent(password)}`, {
        method: 'POST'
    })
    .then(res => res.json())
    .then(data => {
        if (data && data.userId) {
            alert("Login successful!");
            loggedInUserId = data.userId;
            localStorage.setItem('rentalx_userId', data.userId);
            if (document.getElementById('profileSection')) {
                showProfile(data);
            } else {
                window.location.href = 'HomePage.html';
            }
        } else {
            alert("Invalid credentials. Please check your username and password.");
        }
    })
    .catch(() => alert("Login failed. Please check your credentials."));
}

function loadProfile() {
    if (!loggedInUserId) return;
    fetch(`/user/profile/${encodeURIComponent(loggedInUserId)}`, {
        headers: getAuthHeaders()
    })
    .then(res => res.json())
    .then(data => {
        if (data && data.userId) {
            showProfile(data);
        }
    });
}

function showProfile(data) {
    const profileSec = document.getElementById('profileSection');
    if (profileSec) profileSec.style.display = 'block';
    if (document.getElementById('profileName')) document.getElementById('profileName').value = data.name || '';
    if (document.getElementById('profileEmail')) document.getElementById('profileEmail').value = data.email || '';
    if (document.getElementById('profilePhone')) document.getElementById('profilePhone').value = data.phone || '';
}

function updateProfile() {
    if (!loggedInUserId) {
        alert("Please log in first.");
        return;
    }

    const updated = {
        userId: loggedInUserId,
        name: document.getElementById('profileName').value.trim(),
        email: document.getElementById('profileEmail').value.trim(),
        phone: document.getElementById('profilePhone').value.trim(),
        password: ""
    };

    fetch(`/user/profile/${encodeURIComponent(loggedInUserId)}`, { headers: getAuthHeaders() })
        .then(res => res.json())
        .then(old => {
            updated.password = old.password;

            fetch('/user/update', {
                method: 'PUT',
                headers: getAuthHeaders(),
                body: JSON.stringify(updated)
            }).then(() => {
                alert("Profile updated successfully!");
                localStorage.setItem('rentalx_name', updated.name);
            });
        });
}

function deleteProfile() {
    if (!confirm("Are you sure you want to delete your account? This action cannot be undone.")) return;

    fetch(`/user/delete/${encodeURIComponent(loggedInUserId)}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
    }).then(() => {
        alert("Account deleted successfully.");
        localStorage.clear();
        location.reload();
    });
}

function logout() {
    localStorage.clear();
    alert("Logged out successfully.");
    window.location.href = 'HomePage.html';
}
