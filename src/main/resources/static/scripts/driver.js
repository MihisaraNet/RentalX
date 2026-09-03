const DRIVER_API = '/driver';

function getAuthHeaders() {
    const token = localStorage.getItem('rentalx_token');
    const headers = { 'Content-Type': 'application/json' };
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
}

const addDriverForm = document.getElementById('addDriverForm');
if (addDriverForm) {
    addDriverForm.addEventListener('submit', function (e) {
        e.preventDefault();

        const driver = {
            driverId: document.getElementById('driverId').value.trim(),
            name: document.getElementById('name').value.trim(),
            licenseNumber: document.getElementById('licenseNumber').value.trim(),
            phone: document.getElementById('phone').value.trim(),
            email: document.getElementById('email').value.trim(),
            password: document.getElementById('password').value
        };

        fetch(`${DRIVER_API}/register`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(driver)
        })
        .then(res => {
            if (!res.ok) throw new Error('Registration failed');
            alert('Driver added successfully!');
            addDriverForm.reset();
            fetchDrivers();
        })
        .catch(err => {
            console.error(err);
            alert("Error adding driver: " + err.message);
        });
    });
}

function fetchDrivers() {
    fetch(`${DRIVER_API}/all`, { headers: getAuthHeaders() })
        .then(res => res.json())
        .then(data => {
            const tableBody = document.getElementById('driverTableBody');
            if (!tableBody) return;
            tableBody.innerHTML = '';

            if (!Array.isArray(data) || data.length === 0) {
                tableBody.innerHTML = '<tr><td colspan="6" style="text-align:center; padding:15px;">No drivers registered yet.</td></tr>';
                return;
            }

            data.forEach(d => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td><strong>${d.driverId}</strong></td>
                    <td>${d.name}</td>
                    <td>${d.licenseNumber || 'N/A'}</td>
                    <td>${d.phone || 'N/A'}</td>
                    <td>${d.email}</td>
                    <td><button onclick="deleteDriver('${d.driverId}')" style="background:#fee2e2; color:#dc2626; border:1px solid #fca5a5; padding:4px 8px; border-radius:4px; cursor:pointer;">Delete</button></td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(err => {
            console.error("Error fetching drivers:", err);
        });
}

function deleteDriver(driverId) {
    if (!confirm(`Are you sure you want to delete driver ID: ${driverId}?`)) return;

    fetch(`${DRIVER_API}/delete/${driverId}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
    })
    .then(res => {
        if (!res.ok) throw new Error('Delete failed');
        alert('Driver deleted!');
        fetchDrivers();
    })
    .catch(err => {
        console.error(err);
        alert("Error deleting driver.");
    });
}

window.onload = fetchDrivers;
