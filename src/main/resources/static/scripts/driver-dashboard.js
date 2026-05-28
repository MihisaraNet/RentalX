const BASE_URL = '/vehicles';
const DRIVER_API = '/driver'; // Define DRIVER_API constant

// Add Vehicle Form Submission
document.getElementById('addVehicleForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const form = document.getElementById('addVehicleForm');
    const formData = new FormData(form);

    formData.set("available", form.elements['available'].checked);
    formData.set("driverId", sessionStorage.getItem("driverId"));

    fetch(`${BASE_URL}/add`, {
        method: 'POST',
        body: formData
    }).then(() => {
        alert("Vehicle added successfully!");
        form.reset();
        fetchMyVehicles();
    }).catch(err => {
        console.error(err);
        alert("Error adding vehicle.");
    });
});

// Fetch and display driver-specific vehicles
function fetchMyVehicles() {
    const driverId = sessionStorage.getItem("driverId");
    if (!driverId) {
        alert("Please log in to view your vehicles.");
        return;
    }

    fetch(`${BASE_URL}/driver/${driverId}`)
        .then(res => res.json())
        .then(data => {
            const container = document.getElementById('myVehicles');
            container.innerHTML = '';

            if (data.length === 0) {
                container.innerHTML = '<p>No vehicles found.</p>';
                return;
            }

            data.forEach(v => {
                container.innerHTML += `
                    <div class="vehicle-card">
                        <strong>ID:</strong> ${v.vehicleId}<br>
                        <strong>Model:</strong> ${v.model}<br>
                        <strong>Type:</strong> ${v.type}<br>
                        <strong>Available:</strong> ${v.available ? 'Yes' : 'No'}<br>
                        <strong>Rent:</strong> Rs.${v.rentPrice}<br>
                        <img src="${v.imagePath}" width="200" style="margin-top: 10px;"><br>
                        <button onclick="populateUpdateForm('${v.vehicleId}', '${v.model}', '${v.type}', ${v.rentPrice}, '${v.imagePath}', ${v.available})">Edit</button>
                        <button onclick="deleteVehicle('${v.vehicleId}')" style="background-color: red;">Delete</button>
                    </div>
                `;
            });
        })
        .catch(err => {
            console.error(err);
            alert("Error fetching vehicles.");
        });
}

// Fetch and populate driver profile
function fetchDriverProfile() {
    const driverId = sessionStorage.getItem("driverId");
    if (!driverId) {
        alert("Please log in to view your profile.");
        return;
    }

    fetch(`${DRIVER_API}/profile/${driverId}`)
        .then(res => res.json())
        .then(data => {
            if (data) {
                document.getElementById('profileName').value = data.name || '';
                document.getElementById('profileLicense').value = data.licenseNumber || '';
                document.getElementById('profilePhone').value = data.phone || '';
                document.getElementById('profileEmail').value = data.email || '';
            } else {
                alert("Profile not found.");
            }
        })
        .catch(err => {
            console.error(err);
            alert("Error fetching profile.");
        });
}

// Update driver profile
function updateProfile() {
    const driverId = sessionStorage.getItem("driverId");
    if (!driverId) {
        alert("Please log in to update your profile.");
        return;
    }

    const updatedDriver = {
        driverId: driverId,
        name: document.getElementById('profileName').value,
        licenseNumber: document.getElementById('profileLicense').value,
        phone: document.getElementById('profilePhone').value,
        email: document.getElementById('profileEmail').value,
        password: "" // Will be filled with existing password
    };

    // Fetch current profile to get existing password
    fetch(`${DRIVER_API}/profile/${driverId}`)
        .then(res => res.json())
        .then(currentDriver => {
            if (!currentDriver) {
                throw new Error("Profile not found.");
            }
            updatedDriver.password = currentDriver.password; // Preserve password
            return fetch(`${DRIVER_API}/update`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(updatedDriver)
            });
        })
        .then(res => {
            if (res.ok) {
                alert("Profile updated successfully!");
                fetchDriverProfile(); // Refresh profile display
            } else {
                alert("Update failed.");
            }
        })
        .catch(err => {
            console.error(err);
            alert("Error updating profile.");
        });
}

// Populate update form with vehicle data
function populateUpdateForm(vehicleId, model, type, rentPrice, imagePath, available) {
    document.getElementById('updateVehicleId').value = vehicleId;
    document.getElementById('updateModel').value = model;
    document.getElementById('updateType').value = type;
    document.getElementById('updateRentPrice').value = rentPrice;
    document.getElementById('updateImagePath').value = imagePath;
    document.getElementById('updateAvailable').checked = available;
}

// Delete driver profile
function deleteProfile() {
    const driverId = sessionStorage.getItem("driverId");
    if (!driverId) {
        alert("Please log in to delete your profile.");
        return;
    }

    if (!confirm("Are you sure you want to delete your profile? This cannot be undone.")) {
        return;
    }

    fetch(`${DRIVER_API}/delete/${driverId}`, {
        method: 'DELETE'
    })
        .then(res => {
            if (res.ok) {
                alert("Profile deleted successfully!");
                sessionStorage.removeItem("driverId");
                window.location.href = "driver-login.html";
            } else {
                alert("Deletion failed.");
            }
        })
        .catch(err => {
            console.error(err);
            alert("Error deleting profile.");
        });
}

// Delete Vehicle
function deleteVehicle(vehicleId) {
    if (!confirm("Are you sure you want to delete this vehicle?")) return;

    fetch(`${BASE_URL}/delete/${vehicleId}`, {
        method: 'DELETE'
    }).then(() => {
        alert('Vehicle deleted!');
        fetchMyVehicles();
    }).catch(err => {
        console.error(err);
        alert("Error deleting vehicle.");
    });
}

// Update Vehicle Form Submission
document.getElementById('updateVehicleForm').addEventListener('submit', function (e) {
    e.preventDefault();

    const form = document.getElementById('updateVehicleForm');
    const formData = new FormData(form);

    formData.set("available", form.elements['available'].checked);
    const driverId = sessionStorage.getItem("driverId");
    if (!driverId) {
        alert("Please log in to update a vehicle.");
        return;
    }
    formData.set("driverId", driverId);

    fetch(`/vehicles/update/${formData.get('vehicleId')}`, {
        method: 'PUT',
        body: formData
    }).then(res => {
        if (res.ok) {
            alert("Vehicle updated successfully!");
            fetchMyVehicles();
            document.getElementById('updateVehicleForm').reset();
        } else {
            alert("Update failed.");
        }
    }).catch(err => {
        console.error(err);
        alert("Error updating vehicle.");
    });
});

// Load vehicles and profile when the page loads
window.onload = function () {
    fetchMyVehicles();
    fetchDriverProfile(); // Fetch profile on page load
};