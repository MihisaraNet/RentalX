function getAuthHeaders() {
    const token = localStorage.getItem('rentalx_token');
    const headers = { 'Content-Type': 'application/json' };
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
}

function fetchVehicles() {
    fetch('/vehicles/all?sortByPrice=true', { headers: getAuthHeaders() })
        .then(res => res.json())
        .then(data => {
            const grid = document.getElementById('vehicleGrid');
            const vehicleSelect = document.getElementById('vehicleId');
            if (grid) grid.innerHTML = '';
            if (vehicleSelect) vehicleSelect.innerHTML = '<option value="">Select Vehicle</option>';

            if (!Array.isArray(data)) return;

            data.forEach(v => {
                const defaultImg = "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=400";
                const imgSrc = v.imagePath && v.imagePath.trim() !== '' ? v.imagePath : defaultImg;

                if (grid) {
                    grid.innerHTML += `
                        <div class="card" style="border: 1px solid #e5e7eb; border-radius: 8px; padding: 12px; margin-bottom: 10px;">
                            <img src="${imgSrc}" alt="${v.model}" style="width: 100%; height: 140px; object-fit: cover; border-radius: 6px;" onerror="this.src='${defaultImg}'">
                            <h4 style="margin: 8px 0 4px 0;">${v.model}</h4>
                            <div style="font-size: 13px; color: #6b7280;">Type: ${v.type}</div>
                            <div style="font-size: 14px; font-weight: bold; color: #1f2937; margin: 4px 0;">Rent: $${v.rentPrice}/day</div>
                            <span style="display:inline-block; font-size:12px; font-weight:600; padding:2px 6px; border-radius:4px; color:${v.available ? '#059669' : '#dc2626'}; background:${v.available ? '#d1fae5' : '#fee2e2'};">
                                ${v.available ? 'Available' : 'Rented'}
                            </span>
                        </div>`;
                }

                if (vehicleSelect && v.available) {
                    vehicleSelect.innerHTML += `<option value="${v.vehicleId}">${v.model} ($${v.rentPrice}/day)</option>`;
                }
            });
        })
        .catch(err => console.error("Error fetching vehicles:", err));
}

function fetchDrivers() {
    fetch('/driver/all', { headers: getAuthHeaders() })
        .then(res => res.json())
        .then(data => {
            const driverSelect = document.getElementById('driverId');
            if (!driverSelect) return;
            driverSelect.innerHTML = '<option value="">Select Driver</option>';
            if (Array.isArray(data)) {
                data.forEach(d => {
                    driverSelect.innerHTML += `<option value="${d.driverId}">${d.name}</option>`;
                });
            }
        })
        .catch(err => console.error("Error fetching drivers:", err));
}

const bookingForm = document.getElementById('userBookingForm');
if (bookingForm) {
    bookingForm.addEventListener('submit', function (e) {
        e.preventDefault();

        const booking = {
            bookingId: "BK-" + Date.now(),
            userId: document.getElementById('userId').value.trim(),
            vehicleId: document.getElementById('vehicleId').value.trim(),
            driverId: document.getElementById('driverId') ? document.getElementById('driverId').value.trim() : "",
            bookingDate: document.getElementById('bookingDate').value,
            returnDate: document.getElementById('returnDate').value,
            status: "Pending"
        };

        fetch('/bookings/add', {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(booking)
        })
        .then(async res => {
            const result = await res.json().catch(() => null);
            if (res.ok) {
                alert("🎉 Booking submitted successfully! Awaiting approval.");
                bookingForm.reset();
                fetchVehicles();
            } else {
                alert(result && result.message ? result.message : "Booking could not be created.");
            }
        })
        .catch(err => alert("Error adding booking: " + err));
    });
}

function fetchUserBookings() {
    const uidInput = document.getElementById('myUserId');
    const uid = uidInput ? uidInput.value.trim() : localStorage.getItem('rentalx_userId');
    if (!uid) return alert("Please enter or log in with your User ID");

    fetch(`/bookings/user/${encodeURIComponent(uid)}`, { headers: getAuthHeaders() })
        .then(res => res.json())
        .then(data => {
            const list = document.getElementById('myBookingsList');
            if (!list) return;
            list.innerHTML = '';

            if (!Array.isArray(data) || data.length === 0) {
                list.innerHTML = '<p style="color: #6b7280; text-align: center;">No bookings found for this account.</p>';
                return;
            }

            data.forEach(b => {
                const statusColor = b.status === 'Approved' ? '#10b981' :
                                    b.status === 'Pending' ? '#f59e0b' :
                                    b.status === 'Completed' ? '#3b82f6' : '#ef4444';

                list.innerHTML += `
                    <div class="card" style="border-left: 4px solid ${statusColor}; padding: 12px; margin-bottom: 10px; background: #fff; border-radius: 8px;">
                        <div style="display:flex; justify-content:space-between;">
                            <strong>Booking: #${b.bookingId}</strong>
                            <span style="font-weight:bold; color:${statusColor};">${b.status}</span>
                        </div>
                        <div>Vehicle: ${b.vehicleId} | Driver: ${b.driverId || 'Self-Drive'}</div>
                        <div>Duration: ${b.bookingDate} → ${b.returnDate}</div>
                        ${b.totalCost ? `<div>Total: $${b.totalCost}</div>` : ''}
                    </div>
                `;
            });
        });
}

window.onload = () => {
    // Auto populate logged in user ID if available
    const savedUser = localStorage.getItem('rentalx_userId');
    if (savedUser) {
        if (document.getElementById('userId')) document.getElementById('userId').value = savedUser;
        if (document.getElementById('myUserId')) document.getElementById('myUserId').value = savedUser;
    }
    fetchVehicles();
    fetchDrivers();
};
