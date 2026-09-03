const BASE_URL = '/vehicles';

function getAuthHeaders() {
    const token = localStorage.getItem('rentalx_token');
    const headers = {};
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
}

const addForm = document.getElementById('addVehicleForm');
if (addForm) {
    addForm.addEventListener('submit', function (e) {
        e.preventDefault();
        const formData = new FormData(addForm);
        formData.set("available", addForm.elements['available'] ? addForm.elements['available'].checked : true);

        fetch(`${BASE_URL}/add`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: formData
        })
        .then(async response => {
            const text = await response.text();
            if (!response.ok) {
                throw new Error(text || "Failed to add vehicle.");
            }
            alert("Vehicle added successfully!");
            addForm.reset();
            fetchVehicles(false);
        })
        .catch(err => {
            console.error("Add failed:", err.message);
            alert(err.message || "Failed to add vehicle.");
        });
    });
}

function fetchVehicles(sortByPrice = false) {
    fetch(`${BASE_URL}/all?sortByPrice=${sortByPrice}`, {
        headers: getAuthHeaders()
    })
    .then(res => res.json())
    .then(data => {
        const tbody = document.getElementById('vehicleTableBody');
        if (!tbody) return;
        tbody.innerHTML = '';

        if (!Array.isArray(data) || data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" style="text-align:center; padding: 20px;">No vehicles found.</td></tr>';
            return;
        }

        data.forEach(v => {
            const row = document.createElement('tr');
            const defaultImg = "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=400";
            const imgSrc = v.imagePath && v.imagePath.trim() !== '' ? v.imagePath : defaultImg;

            row.innerHTML = `
                <td><strong>${v.vehicleId}</strong></td>
                <td>${v.model}</td>
                <td><span class="badge" style="background:#e0e7ff; color:#3730a3; padding:2px 6px; border-radius:4px;">${v.type}</span></td>
                <td>
                    <span style="color: ${v.available ? '#10b981' : '#ef4444'}; font-weight: bold;">
                        ${v.available ? '● Available' : '● Rented'}
                    </span>
                </td>
                <td><strong>$${v.rentPrice}</strong>/day</td>
                <td><img src="${imgSrc}" alt="${v.model}" style="width: 70px; height: 45px; object-fit: cover; border-radius: 6px;" onerror="this.src='${defaultImg}'"></td>
                <td>${v.driverId || 'N/A'}</td>
                <td>
                    <button class="action-btn btn-toggle" onclick="toggleAvailability('${v.vehicleId}')" style="margin-right: 5px; cursor: pointer;">
                        ${v.available ? 'Mark Rented' : 'Mark Available'}
                    </button>
                    <button class="action-btn btn-delete" onclick="deleteVehicleById('${v.vehicleId}')" style="cursor: pointer; background: #fee2e2; color: #dc2626; border: 1px solid #fca5a5;">
                        Delete
                    </button>
                </td>
            `;
            tbody.appendChild(row);
        });
    })
    .catch(err => {
        console.error(err);
    });
}

function deleteVehicleById(id) {
    if (!confirm(`Are you sure you want to delete vehicle ID: ${id}?`)) return;

    fetch(`${BASE_URL}/delete/${id}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
    }).then(() => {
        alert('Vehicle deleted successfully!');
        fetchVehicles(false);
    }).catch(err => {
        console.error(err);
        alert("Failed to delete vehicle.");
    });
}

function toggleAvailability(vehicleId) {
    fetch(`${BASE_URL}/toggleAvailability/${vehicleId}`, {
        method: 'PUT',
        headers: getAuthHeaders()
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
        method: 'POST',
        headers: getAuthHeaders()
    })
    .then(res => res.text())
    .then(msg => {
        alert(msg);
        fetchVehicles(false);
    })
    .catch(err => console.error("Error renting:", err));
}

window.onload = () => {
    fetchVehicles(false);
};
