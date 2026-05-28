const BASE_URL = '/reviews';

document.getElementById('addReviewForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const review = {
        reviewId: document.getElementById('reviewId').value,
        userId: document.getElementById('userId').value,
        vehicleId: document.getElementById('vehicleId').value,
        reviewText: document.getElementById('reviewText').value,
        rating: parseInt(document.getElementById('rating').value)
    };

    fetch(`${BASE_URL}/add`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(review)
    }).then(() => {
        alert('Review added!');
        fetchReviews();
    });
});

function fetchReviews() {
    fetch(`${BASE_URL}/all`)
        .then(response => response.json())
        .then(data => {
            const list = document.getElementById('reviewList');
            list.innerHTML = '';
            data.forEach(r => {
                list.innerHTML += `
                    <div class="card">
                        <strong>ID:</strong> ${r.reviewId} <br>
                        <strong>User:</strong> ${r.userId} <br>
                        <strong>Vehicle:</strong> ${r.vehicleId} <br>
                        <strong>Text:</strong> ${r.reviewText} <br>
                        <strong>Rating:</strong> ${r.rating}/5
                        <hr>
                    </div>`;
            });
        });
}

document.getElementById('updateReviewForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const id = document.getElementById('updateReviewId').value;
    const review = {
        reviewId: id,
        userId: document.getElementById('updateUserId').value,
        vehicleId: document.getElementById('updateVehicleId').value,
        reviewText: document.getElementById('updateReviewText').value,
        rating: parseInt(document.getElementById('updateRating').value)
    };

    fetch(`${BASE_URL}/update/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(review)
    }).then(() => {
        alert('Review updated!');
        fetchReviews();
    });
});

function deleteReview() {
    const id = document.getElementById('deleteReviewId').value;
    if (!id) return alert('Enter a Review ID');

    fetch(`${BASE_URL}/delete/${id}`, {
        method: 'DELETE'
    }).then(() => {
        alert('Review deleted!');
        fetchReviews();
    });
}
