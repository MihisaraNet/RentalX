const BOOKING_API = '/bookings';

function getAuthHeaders() {
    const token = localStorage.getItem('rentalx_token');
    const headers = { 'Content-Type': 'application/json' };
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
}

document.addEventListener('DOMContentLoaded', () => {
    // Auto-fill logged in user
    const loggedUser = localStorage.getItem('rentalx_userId');
    if (loggedUser && document.getElementById('userId')) {
        document.getElementById('userId').value = loggedUser;
    }
    fetchBookings();
});

const addBookingForm = document.getElementById('addBookingForm');
if (addBookingForm) {
    addBookingForm.addEventListener('submit', function (e) {
        e.preventDefault();

        const booking = {
            bookingId: document.getElementById('bookingId') ? document.getElementById('bookingId').value.trim() : "",
            userId: document.getElementById('userId').value.trim(),
            vehicleId: document.getElementById('vehicleId').value.trim(),
            driverId: document.getElementById('driverId') ? document.getElementById('driverId').value.trim() : "",
            bookingDate: document.getElementById('bookingDate').value,
            returnDate: document.getElementById('returnDate').value,
            status: document.getElementById('status') ? document.getElementById('status').value : "Pending"
        };

        fetch(`${BOOKING_API}/add`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(booking)
        })
        .then(async res => {
            const result = await res.json().catch(() => null);
            if (res.ok) {
                alert("🎉 Booking confirmed successfully!");
                addBookingForm.reset();
                fetchBookings();
            } else {
                alert(result && result.message ? result.message : "Booking could not be created.");
            }
        })
        .catch(err => {
            alert("Error placing booking: " + err);
        });
    });
}

function fetchBookings() {
    const list = document.getElementById('bookingList');
    if (!list) return;

    fetch(`${BOOKING_API}/all`, { headers: getAuthHeaders() })
        .then(res => res.json())
        .then(data => {
            list.innerHTML = '';
            if (!Array.isArray(data) || data.length === 0) {
                list.innerHTML = '<p style="color: #666; text-align: center;">No bookings found.</p>';
                return;
            }

            data.forEach(b => {
                const statusColor = b.status === 'Approved' ? '#10b981' :
                                    b.status === 'Pending' ? '#f59e0b' :
                                    b.status === 'Completed' ? '#3b82f6' : '#ef4444';

                list.innerHTML += `
                    <div class="booking-card" style="border-left: 4px solid ${statusColor}; padding: 12px; margin-bottom: 10px; background: #fff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
                            <strong>Booking #${b.bookingId}</strong>
                            <span style="background: ${statusColor}20; color: ${statusColor}; padding: 3px 8px; border-radius: 12px; font-weight: bold; font-size: 12px;">${b.status}</span>
                        </div>
                        <div><strong>Customer:</strong> ${b.userId} | <strong>Vehicle:</strong> ${b.vehicleId}</div>
                        <div><strong>Duration:</strong> ${b.bookingDate} → ${b.returnDate}</div>
                        ${b.totalCost ? `<div><strong>Total Cost:</strong> $${b.totalCost}</div>` : ''}
                        ${b.paymentStatus ? `<div><strong>Payment:</strong> ${b.paymentStatus}</div>` : ''}
                        <div style="margin-top: 8px;">
                            <button onclick="updateBookingStatus('${b.bookingId}', 'Approved')" style="background: #10b981; color: white; border: none; padding: 4px 8px; border-radius: 4px; cursor: pointer; font-size: 11px;">Approve</button>
                            <button onclick="updateBookingStatus('${b.bookingId}', 'Rejected')" style="background: #ef4444; color: white; border: none; padding: 4px 8px; border-radius: 4px; cursor: pointer; font-size: 11px;">Reject</button>
                            <button onclick="updateBookingStatus('${b.bookingId}', 'Completed')" style="background: #3b82f6; color: white; border: none; padding: 4px 8px; border-radius: 4px; cursor: pointer; font-size: 11px;">Complete</button>
                        </div>
                    </div>
                `;
            });
        })
        .catch(() => {});
}

function updateBookingStatus(bookingId, status) {
    const endpoint = status === 'Approved' ? `/bookings/approve/${bookingId}` :
                     status === 'Rejected' ? `/bookings/reject/${bookingId}` :
                     `/bookings/complete/${bookingId}`;

    fetch(endpoint, {
        method: 'PUT',
        headers: getAuthHeaders()
    }).then(() => {
        alert(`Booking status changed to: ${status}`);
        fetchBookings();
    });
}

const updateForm = document.getElementById('updateBookingForm');
if (updateForm) {
    updateForm.addEventListener('submit', function (e) {
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
            headers: getAuthHeaders(),
            body: JSON.stringify(updated)
        }).then(() => {
            alert("Booking updated!");
            fetchBookings();
        });
    });
}

function deleteBooking() {
    const id = document.getElementById('deleteBookingId').value;
    if (!id) return alert("Enter Booking ID");

    fetch(`${BOOKING_API}/delete/${id}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
    }).then(() => {
        alert("Booking deleted!");
        fetchBookings();
    });
}
