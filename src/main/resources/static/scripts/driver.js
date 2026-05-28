const DRIVER_API = '/driver';

document.getElementById('addDriverForm').addEventListener('submit', function (e) {
    e.preventDefault();

    const driver = {
        driverId: document.getElementById('driverId').value,
        name: document.getElementById('name').value,
        licenseNumber: document.getElementById('licenseNumber').value,
        phone: document.getElementById('phone').value,
        email: document.getElementById('email').value,
        password: document.getElementById('password').value
    };

    fetch(`${DRIVER_API}/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(driver)
    })
        .then(res => {
            if (!res.ok) throw new Error('Registration failed');
            alert('Driver added!');
            document.getElementById('addDriverForm').reset();
            fetchDrivers();
        })
        .catch(err => {
            console.error(err);
            alert("Error adding driver.");
        });
});

function fetchDrivers() {
    fetch(`${DRIVER_API}/all`)
        .then(res => res.json())
        .then(data => {
            const tableBody = document.getElementById('driverTableBody');
            tableBody.innerHTML = '';

            data.forEach(d => {
                const row = document.createElement('tr');

                row.innerHTML = `
                    <td>${d.driverId}</td>
                    <td>${d.name}</td>
                    <td>${d.licenseNumber}</td>
                    <td>${d.phone}</td>
                    <td>${d.email}</td>
                    <td><button onclick="deleteDriver('${d.driverId}')">Delete</button></td>
                `;

                tableBody.appendChild(row);
            });
        })
        .catch(err => {
            console.error("Error fetching drivers:", err);
            alert("Could not load driver list.");
        });
}

function deleteDriver(driverId) {
    if (!confirm(`Are you sure you want to delete driver ID: ${driverId}?`)) return;

    fetch(`${DRIVER_API}/delete/${driverId}`, {
        method: 'DELETE'
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
