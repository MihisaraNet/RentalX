let loggedInUserId = "";

document.getElementById('registerForm').addEventListener('submit', function (e) {
    e.preventDefault();

    const user = {
        userId: document.getElementById('regId').value,
        name: document.getElementById('regName').value,
        email: document.getElementById('regEmail').value,
        phone: document.getElementById('regPhone').value,
        password: document.getElementById('regPassword').value
    };

    fetch('/user/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(user)
    }).then(() => {
        alert("Registered successfully!");
        document.getElementById('registerForm').reset();
    });
});

document.getElementById('loginForm').addEventListener('submit', function (e) {
    e.preventDefault();

    const userId = document.getElementById('loginId').value;
    const password = document.getElementById('loginPassword').value;

    fetch(`/user/login?userId=${userId}&password=${password}`, {
        method: 'POST'
    })
        .then(res => res.json())
        .then(data => {
            if (data && data.userId) {
                alert("Login successful!");
                loggedInUserId = data.userId;
                showProfile(data);
            } else {
                alert("Invalid credentials.");
            }
        });
});

function showProfile(data) {
    document.getElementById('profileSection').style.display = 'block';
    document.getElementById('profileName').value = data.name;
    document.getElementById('profileEmail').value = data.email;
    document.getElementById('profilePhone').value = data.phone;
}

function updateProfile() {
    const updated = {
        userId: loggedInUserId,
        name: document.getElementById('profileName').value,
        email: document.getElementById('profileEmail').value,
        phone: document.getElementById('profilePhone').value,
        password: "" // will fill next
    };

    // fetch old profile to preserve password
    fetch(`/user/profile/${loggedInUserId}`)
        .then(res => res.json())
        .then(old => {
            updated.password = old.password;

            fetch('/user/update', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(updated)
            }).then(() => {
                alert("Profile updated!");
            });
        });
}

function deleteProfile() {
    if (!confirm("Are you sure you want to delete your account?")) return;

    fetch(`/user/delete/${loggedInUserId}`, {
        method: 'DELETE'
    }).then(() => {
        alert("Account deleted.");
        location.reload();
    });
}
