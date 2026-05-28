const BASE_URL = '/vehicles';

document.getElementById('addVehicleForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const form = document.getElementById('addVehicleForm');
    const formData = new FormData(form);

    // Convert checkbox to boolean
    formData.set("available", form.elements['available'].checked);

    fetch(`${BASE_URL}/add`, {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(msg => {
                    throw new Error(msg); // Trigger catch with custom error
                });
            }
            return response.text(); // Optional: Success message
        })
        .then(() => {
            alert("Vehicle added successfully!");
            form.reset();
            fetchVehicles(false);
        })
        .catch(err => {
            console.error("Add failed:", err.message);
            alert(err.message || "Failed to add vehicle.");
        });
});


function fetchVehicles(sort) {
    fetch(`${BASE_URL}/all?sortByPrice=${sort}`)
        .then(res => res.json())
        .then(data => {
            const tbody = document.getElementById('vehicleTableBody');
            tbody.innerHTML = '';

            data.forEach(v => {
                const row = document.createElement('tr');
                row.innerHTML = `
        <td>${v.vehicleId}</td>
        <td>${v.model}</td>
        <td>${v.type}</td>
        <td style="color: ${v.available ? 'green' : 'red'};">
            ${v.available ? 'Available' : 'Rented'}
        </td>
        <td>Rs.${v.rentPrice}</td>
        <td><img src="${v.imagePath}" alt="Vehicle Image"></td>
        <td>${v.driverId}</td>
<td>
  <button class="action-btn btn-toggle" onclick="toggleAvailability('${v.vehicleId}')">
    ${v.available ? 'Mark Rented' : 'Mark Available'}
  </button>
</td>

<td>
  <button class="action-btn btn-delete" onclick="deleteVehicleById('${v.vehicleId}')">
    Delete
  </button>
</td>


    `;
                tbody.appendChild(row);
            });

        })
        .catch(err => {
            console.error(err);
            alert("Failed to load vehicles.");
        });
}

function deleteVehicleById(id) {
    if (!confirm(`Are you sure you want to delete vehicle ID: ${id}?`)) return;

    fetch(`${BASE_URL}/delete/${id}`, {
        method: 'DELETE'
    }).then(() => {
        alert('Vehicle deleted!');
        fetchVehicles(false);
    }).catch(err => {
        console.error(err);
        alert("Failed to delete vehicle.");
    });
}

function toggleAvailability(vehicleId) {
    fetch(`${BASE_URL}/toggleAvailability/${vehicleId}`, {
        method: 'PUT'
    })
        .then(res => res.text())
        .then(msg => {
            alert(msg);
            fetchVehicles(false);
        })
        .catch(err => {
            console.error(err);
            alert("Error toggling availability.");
        });
}


function rentVehicle(vehicleId) {
    fetch(`/vehicles/rent/${vehicleId}`, {
        method: 'POST'
    })
        .then(res => res.text())
        .then(msg => {
            alert(msg);
            fetchVehicles(false); // Refresh list
        })
        .catch(err => console.error("Error renting:", err));
}


// ✅ Keep this logic if you later add update UI
document.getElementById('updateVehicleForm')?.addEventListener('submit', function (e) {
    e.preventDefault();

    const vehicleId = document.getElementById('updateVehicleId').value;
    const updatedVehicle = {
        vehicleId: vehicleId,
        model: document.getElementById('updateModel').value,
        type: document.getElementById('updateType').value,
        available: document.getElementById('updateAvailable').checked,
        rentPrice: parseFloat(document.getElementById('updateRentPrice').value),
        imagePath: "" // placeholder
    };

    fetch(`${BASE_URL}/update/${vehicleId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updatedVehicle)
    }).then(res => {
        if (res.ok) {
            alert("Vehicle updated successfully!");
            fetchVehicles(false);
        } else {
            alert("Update failed.");
        }
    }).catch(err => {
        console.error(err);
        alert("Error updating vehicle.");
    });
});

window.onload = () => fetchVehicles(false);
