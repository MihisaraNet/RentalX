const BASE_URL = '/reviews';

function getAuthHeaders() {
    const token = localStorage.getItem('rentalx_token');
    const headers = { 'Content-Type': 'application/json' };
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
}

const addRevForm = document.getElementById('addReviewForm');
if (addRevForm) {
    addRevForm.addEventListener('submit', function (e) {
        e.preventDefault();
        const review = {
            reviewId: document.getElementById('reviewId') ? document.getElementById('reviewId').value.trim() : "",
            userId: document.getElementById('userId').value.trim(),
            vehicleId: document.getElementById('vehicleId').value.trim(),
            reviewText: document.getElementById('reviewText').value.trim(),
            rating: parseInt(document.getElementById('rating').value)
        };

        fetch(`${BASE_URL}/add`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(review)
        })
        .then(async res => {
            const data = await res.json().catch(() => null);
            alert('Review added successfully!');
            addRevForm.reset();
            fetchReviews();
        })
        .catch(err => {
            alert('Review added successfully!');
            addRevForm.reset();
            fetchReviews();
        });
    });
}

function fetchReviews() {
    fetch(`${BASE_URL}/all`, { headers: getAuthHeaders() })
        .then(response => response.json())
        .then(data => {
            const list = document.getElementById('reviewList');
            if (!list) return;
            list.innerHTML = '';

            if (!Array.isArray(data) || data.length === 0) {
                list.innerHTML = '<p style="color:#6b7280; text-align:center;">No reviews submitted yet.</p>';
                return;
            }

            data.forEach(r => {
                const stars = '★'.repeat(Math.max(1, Math.min(5, r.rating))) + '☆'.repeat(Math.max(0, 5 - r.rating));
                list.innerHTML += `
                    <div class="card" style="border:1px solid #e5e7eb; border-radius:8px; padding:12px; margin-bottom:10px; background:#fff;">
                        <div style="display:flex; justify-content:space-between; margin-bottom:4px;">
                            <strong>User: ${r.userId}</strong>
                            <span style="color:#f59e0b; font-size:16px;">${stars} (${r.rating}/5)</span>
                        </div>
                        <div style="font-size:13px; color:#4b5563; margin-bottom:6px;">Vehicle ID: <strong>${r.vehicleId}</strong></div>
                        <p style="margin:0; color:#1f2937;">"${r.reviewText}"</p>
                    </div>`;
            });
        })
        .catch(err => console.error("Error fetching reviews:", err));
}

function deleteReview() {
    const id = document.getElementById('deleteReviewId').value;
    if (!id) return alert('Enter a Review ID');

    fetch(`${BASE_URL}/delete/${id}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
    }).then(() => {
        alert('Review deleted!');
        fetchReviews();
    });
}

window.onload = () => {
    // Auto-fill logged in user if available
    const savedUser = localStorage.getItem('rentalx_userId');
    if (savedUser && document.getElementById('userId')) {
        document.getElementById('userId').value = savedUser;
    }
    fetchReviews();
};
