const BOOKING_API = '/bookings';

document.getElementById('addBookingForm').addEventListener('submit', function (e) {
    e.preventDefault();

    const booking = {
        bookingId: document.getElementById('bookingId').value,
        userId: document.getElementById('userId').value,
        vehicleId: document.getElementById('vehicleId').value,
        driverId: document.getElementById('driverId').value,
        bookingDate: document.getElementById('bookingDate').value,
        returnDate: document.getElementById('returnDate').value,
        status: document.getElementById('status').value
    };

    fetch(`${BOOKING_API}/add`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(booking)
    }).then(() => {
        alert("Booking added!");
        document.getElementById('addBookingForm').reset();
        fetchBookings();
    });
});

function fetchBookings() {
    fetch(`${BOOKING_API}/all`)
        .then(res => res.json())
        .then(data => {
            const list = document.getElementById('bookingList');
            list.innerHTML = '';
            data.forEach(b => {
                list.innerHTML += `
                    <div class="booking-card">
                        <strong>ID:</strong> ${b.bookingId}<br>
                        <strong>User:</strong> ${b.userId}<br>
                        <strong>Vehicle:</strong> ${b.vehicleId}<br>
                        <strong>Driver:</strong> ${b.driverId}<br>
                        <strong>Date:</strong> ${b.bookingDate} → ${b.returnDate}<br>
                        <strong>Status:</strong> ${b.status}
                    </div>
                `;
            });
        });
}

document.getElementById('updateBookingForm').addEventListener('submit', function (e) {
    e.preventDefault();

    const id = document.getElementById('updateBookingId').value;

    const updated = {
        bookingId: id,
        userId: document.getElementById('updateUserId').value,
        vehicleId: document.getElementById('updateVehicleId').value,
        driverId: document.getElementById('updateDriverId').value,
        bookingDate: document.getElementById('updateBookingDate').value,
        returnDate: document.getElementById('updateReturnDate').value,
        status: document.getElementById('updateStatus').value
    };

    fetch(`${BOOKING_API}/update/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updated)
    }).then(() => {
        alert("Booking updated!");
        fetchBookings();
    });
});

function deleteBooking() {
    const id = document.getElementById('deleteBookingId').value;
    if (!id) return alert("Enter Booking ID");

    fetch(`${BOOKING_API}/delete/${id}`, {
        method: 'DELETE'
    }).then(() => {
        alert("Booking deleted!");
        fetchBookings();
    });
}

window.onload = fetchBookings;
