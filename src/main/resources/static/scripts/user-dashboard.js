function fetchVehicles() {
    fetch('/vehicles/all?sortByPrice=true')
        .then(res => res.json())
        .then(data => {
            const grid = document.getElementById('vehicleGrid');
            const vehicleSelect = document.getElementById('vehicleId');
            grid.innerHTML = '';
            vehicleSelect.innerHTML = '<option value="">Select Vehicle</option>';

            data.forEach(v => {
                grid.innerHTML += `
                    <div class="card">
                        <img src="${v.imagePath}" alt="${v.model}">
                        <strong>${v.model}</strong><br>
                        Type: ${v.type}<br>
                        Rent: Rs.${v.rentPrice}<br>
                        Available: ${v.available ? 'Yes' : 'No'}
                    </div>`;
                if (v.available) {
                    vehicleSelect.innerHTML += `<option value="${v.vehicleId}">${v.model}</option>`;
                }
            });
        });
}

function fetchDrivers() {
    fetch('/drivers/all')
        .then(res => res.json())
        .then(data => {
            const driverSelect = document.getElementById('driverId');
            driverSelect.innerHTML = '<option value="">Select Driver</option>';
            data.forEach(d => {
                driverSelect.innerHTML += `<option value="${d.driverId}">${d.name}</option>`;
            });
        });
}

document.getElementById('userBookingForm').addEventListener('submit', function (e) {
    e.preventDefault();

    const booking = {
        bookingId: "B" + Date.now(),
        userId: document.getElementById('userId').value,
        vehicleId: document.getElementById('vehicleId').value,
        driverId: document.getElementById('driverId').value,
        bookingDate: document.getElementById('bookingDate').value,
        returnDate: document.getElementById('returnDate').value,
        status: "Confirmed"
    };

    fetch('/bookings/add', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(booking)
    }).then(() => {
        alert("Booking confirmed!");
        document.getElementById('userBookingForm').reset();
    });
});

function fetchUserBookings() {
    const uid = document.getElementById('myUserId').value;
    if (!uid) return alert("Enter User ID");
    fetch('/bookings/all')
        .then(res => res.json())
        .then(data => {
            const filtered = data.filter(b => b.userId === uid);
            const list = document.getElementById('myBookingsList');
            list.innerHTML = '';

            filtered.forEach(b => {
                list.innerHTML += `
                    <div class="card">
                        <strong>Booking:</strong> ${b.bookingId}<br>
                        Vehicle: ${b.vehicleId}<br>
                        Driver: ${b.driverId}<br>
                        ${b.bookingDate} → ${b.returnDate}<br>
                        Status: ${b.status}
                    </div>
                `;
            });
        });
}

window.onload = () => {
    fetchVehicles();
    fetchDrivers();
};
