const BASE_URL = '/vehicles';
const DRIVER_API = '/driver';

function getAuthHeaders() {
    const token = localStorage.getItem('rentalx_token');
    const headers = { 'Content-Type': 'application/json' };
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
}

function getDriverId() {
    return sessionStorage.getItem("driverId") || localStorage.getItem("rentalx_userId") || "";
}

// Add Vehicle Form Submission
const addVehicleForm = document.getElementById('addVehicleForm');
if (addVehicleForm) {
    addVehicleForm.addEventListener('submit', function (e) {
        e.preventDefault();
        const formData = new FormData(addVehicleForm);

        formData.set("available", addVehicleForm.elements['available'].checked);
        formData.set("driverId", getDriverId());

        fetch(`${BASE_URL}/add`, {
            method: 'POST',
            body: formData
        }).then(() => {
            alert("Vehicle added successfully!");
            addVehicleForm.reset();
            fetchMyVehicles();
        }).catch(err => {
            console.error(err);
            alert("Error adding vehicle.");
        });
    });
}

// Fetch and display driver-specific vehicles
function fetchMyVehicles() {
    const driverId = getDriverId();
    if (!driverId) {
        return;
    }

    fetch(`${BASE_URL}/driver/${driverId}`, { headers: getAuthHeaders() })
        .then(res => res.json())
        .then(data => {
            const container = document.getElementById('myVehicles');
            if (!container) return;
            container.innerHTML = '';

            if (!Array.isArray(data) || data.length === 0) {
                container.innerHTML = '<p style="color:#6b7280;">No vehicles currently assigned.</p>';
                return;
            }

            data.forEach(v => {
                const defaultImg = "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=400";
                const imgSrc = v.imagePath && v.imagePath.trim() !== '' ? v.imagePath : defaultImg;

                container.innerHTML += `
                    <div class="vehicle-card" style="border:1px solid #e5e7eb; border-radius:8px; padding:12px; margin-bottom:10px;">
                        <strong>ID:</strong> ${v.vehicleId}<br>
                        <strong>Model:</strong> ${v.model}<br>
                        <strong>Type:</strong> ${v.type}<br>
                        <strong>Available:</strong> ${v.available ? 'Yes' : 'No'}<br>
                        <strong>Rent:</strong> $${v.rentPrice}/day<br>
                        <img src="${imgSrc}" width="160" style="margin-top: 10px; border-radius:6px; object-fit:cover;" onerror="this.src='${defaultImg}'"><br>
                        <button onclick="deleteVehicle('${v.vehicleId}')" style="background-color: #ef4444; color:white; border:none; padding:4px 8px; border-radius:4px; margin-top:8px; cursor:pointer;">Delete</button>
                    </div>
                `;
            });
        })
        .catch(err => {
            console.error(err);
        });
}

// Fetch and populate driver profile
function fetchDriverProfile() {
    const driverId = getDriverId();
    if (!driverId) return;

    fetch(`${DRIVER_API}/profile/${driverId}`, { headers: getAuthHeaders() })
        .then(res => res.json())
        .then(data => {
            if (data) {
                if (document.getElementById('profileName')) document.getElementById('profileName').value = data.name || '';
                if (document.getElementById('profileLicense')) document.getElementById('profileLicense').value = data.licenseNumber || '';
                if (document.getElementById('profilePhone')) document.getElementById('profilePhone').value = data.phone || '';
                if (document.getElementById('profileEmail')) document.getElementById('profileEmail').value = data.email || '';
            }
        })
        .catch(err => console.error(err));
}

// Update driver profile
function updateProfile() {
    const driverId = getDriverId();
    if (!driverId) return alert("Please log in to update your profile.");

    const updatedDriver = {
        driverId: driverId,
        name: document.getElementById('profileName').value,
        licenseNumber: document.getElementById('profileLicense').value,
        phone: document.getElementById('profilePhone').value,
        email: document.getElementById('profileEmail').value,
        password: ""
    };

    fetch(`${DRIVER_API}/update`, {
        method: 'PUT',
        headers: getAuthHeaders(),
        body: JSON.stringify(updatedDriver)
    })
    .then(res => {
        alert("Profile updated successfully!");
        fetchDriverProfile();
    })
    .catch(err => alert("Error updating profile."));
}

// Delete driver profile
function deleteProfile() {
    const driverId = getDriverId();
    if (!driverId) return alert("Please log in first.");

    if (!confirm("Are you sure you want to delete your profile? This cannot be undone.")) return;

    fetch(`${DRIVER_API}/delete/${driverId}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
    })
    .then(res => {
        alert("Profile deleted successfully!");
        sessionStorage.clear();
        localStorage.clear();
        window.location.href = "driver-login.html";
    })
    .catch(err => alert("Error deleting profile."));
}

function deleteVehicle(vehicleId) {
    if (!confirm("Are you sure you want to delete this vehicle?")) return;

    fetch(`${BASE_URL}/delete/${vehicleId}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
    }).then(() => {
        alert('Vehicle deleted!');
        fetchMyVehicles();
    }).catch(err => alert("Error deleting vehicle."));
}

window.onload = function () {
    fetchMyVehicles();
    fetchDriverProfile();
};