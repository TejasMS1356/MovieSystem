const API_BASE = 'http://localhost:8080/api';

// Show section
function showSection(sectionName) {
    document.querySelectorAll('.section').forEach(section => {
        section.classList.remove('active');
    });
    document.getElementById(sectionName).classList.add('active');

    document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');

    // Load data when section is shown
    if (sectionName === 'movies') loadMovies();
    else if (sectionName === 'actors') loadActors();
    else if (sectionName === 'directors') loadDirectors();
    else if (sectionName === 'genres') loadGenres();
    else if (sectionName === 'reviews') loadReviews();
}

// Load all movies
async function loadMovies() {
    const response = await fetch(`${API_BASE}/movies`);
    const movies = await response.json();

    const container = document.getElementById('moviesList');
    container.innerHTML = movies.map(movie => `
        <div class="movie-card">
            <h4>${movie.title} (${movie.release_year})</h4>
            <p><strong>Director:</strong> ${movie.director_name || 'N/A'}</p>
            <p><strong>Genre:</strong> ${movie.genre_name || 'N/A'}</p>
            <p><strong>Language:</strong> ${movie.language}</p>
            <p><strong>Duration:</strong> ${movie.duration_minutes} min</p>
            <p><strong>Rating:</strong> ⭐ ${movie.average_rating || 0}/10 (${movie.total_reviews || 0} reviews)</p>
            <p><strong>Actors:</strong> ${movie.actors || 'N/A'}</p>
            <button onclick="deleteMovie(${movie.movie_id})" class="delete-btn">Delete</button>
        </div>
    `).join('');
}

// Add movie
document.getElementById('movieForm') ? .addEventListener('submit', async(e) => {
    e.preventDefault();
    const data = {
        title: document.getElementById('title').value,
        release_year: parseInt(document.getElementById('year').value),
        duration: parseInt(document.getElementById('duration').value),
        language: document.getElementById('language').value,
        genre_id: parseInt(document.getElementById('genreId').value),
        director_id: parseInt(document.getElementById('directorId').value)
    };

    const response = await fetch(`${API_BASE}/movies`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    const result = await response.json();
    if (result.success) {
        alert('Movie added!');
        e.target.reset();
        loadMovies();
        loadDropdowns();
    }
});

// Delete movie
async function deleteMovie(id) {
    if (confirm('Delete this movie?')) {
        await fetch(`${API_BASE}/movies?id=${id}`, { method: 'DELETE' });
        loadMovies();
    }
}

// Load actors
async function loadActors() {
    const response = await fetch(`${API_BASE}/actors`);
    const actors = await response.json();

    const container = document.getElementById('actorsList');
    container.innerHTML = actors.map(actor => `
        <div class="actor-card">
            <h4>${actor.actor_name}</h4>
            <p><strong>Nationality:</strong> ${actor.nationality || 'N/A'}</p>
            <p><strong>DOB:</strong> ${actor.date_of_birth || 'N/A'}</p>
        </div>
    `).join('');
}

// Add actor
document.getElementById('actorForm') ? .addEventListener('submit', async(e) => {
    e.preventDefault();
    const data = {
        name: document.getElementById('actorName').value,
        dob: document.getElementById('actorDob').value,
        nationality: document.getElementById('actorNationality').value
    };

    const response = await fetch(`${API_BASE}/actors`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    const result = await response.json();
    if (result.success) {
        alert('Actor added!');
        e.target.reset();
        loadActors();
        loadDropdowns();
    }
});

// Link actor to movie
document.getElementById('linkForm') ? .addEventListener('submit', async(e) => {
    e.preventDefault();
    const data = {
        action: 'link',
        movie_id: parseInt(document.getElementById('linkMovieId').value),
        actor_id: parseInt(document.getElementById('linkActorId').value),
        role: document.getElementById('role').value
    };

    const response = await fetch(`${API_BASE}/actors`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    const result = await response.json();
    if (result.success) {
        alert('Actor linked!');
        e.target.reset();
        loadMovies();
    }
});

// Load directors
async function loadDirectors() {
    const response = await fetch(`${API_BASE}/directors`);
    const directors = await response.json();

    const container = document.getElementById('directorsList');
    container.innerHTML = directors.map(director => `
        <div class="director-card">
            <h4>${director.director_name}</h4>
            <p><strong>Nationality:</strong> ${director.nationality || 'N/A'}</p>
            <p><strong>DOB:</strong> ${director.date_of_birth || 'N/A'}</p>
        </div>
    `).join('');
}

// Add director
document.getElementById('directorForm') ? .addEventListener('submit', async(e) => {
    e.preventDefault();
    const data = {
        name: document.getElementById('directorName').value,
        dob: document.getElementById('directorDob').value,
        nationality: document.getElementById('directorNationality').value
    };

    const response = await fetch(`${API_BASE}/directors`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    const result = await response.json();
    if (result.success) {
        alert('Director added!');
        e.target.reset();
        loadDirectors();
        loadDropdowns();
    }
});

// Load genres
async function loadGenres() {
    const response = await fetch(`${API_BASE}/genres`);
    const genres = await response.json();

    const container = document.getElementById('genresList');
    container.innerHTML = genres.map(genre => `
        <div class="genre-card">
            <h4>${genre.genre_name}</h4>
            <p>${genre.description || 'No description'}</p>
        </div>
    `).join('');
}

// Add genre
document.getElementById('genreForm') ? .addEventListener('submit', async(e) => {
    e.preventDefault();
    const data = {
        name: document.getElementById('genreName').value,
        description: document.getElementById('genreDesc').value
    };

    const response = await fetch(`${API_BASE}/genres`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    const result = await response.json();
    if (result.success) {
        alert('Genre added!');
        e.target.reset();
        loadGenres();
        loadDropdowns();
    }
});

// Load reviews
async function loadReviews() {
    const response = await fetch(`${API_BASE}/reviews`);
    const reviews = await response.json();

    const container = document.getElementById('reviewsList');
    container.innerHTML = reviews.map(review => `
        <div class="review-card">
            <h4>${review.movie_title}</h4>
            <p><strong>Reviewer:</strong> ${review.reviewer_name}</p>
            <p><strong>Rating:</strong> ⭐ ${review.rating}/10</p>
            <p><strong>Review:</strong> ${review.review_text || 'No review text'}</p>
            <p><small>${new Date(review.review_date).toLocaleDateString()}</small></p>
        </div>
    `).join('');
}

// Add review
document.getElementById('reviewForm') ? .addEventListener('submit', async(e) => {
    e.preventDefault();
    const data = {
        movie_id: parseInt(document.getElementById('reviewMovieId').value),
        reviewer_name: document.getElementById('reviewerName').value,
        rating: parseFloat(document.getElementById('rating').value),
        review_text: document.getElementById('reviewText').value
    };

    const response = await fetch(`${API_BASE}/reviews`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    const result = await response.json();
    if (result.success) {
        alert('Review added!');
        e.target.reset();
        loadReviews();
        loadMovies();
    }
});

// Load top rated movies
async function loadTopRated() {
    const response = await fetch(`${API_BASE}/reports`);
    const movies = await response.json();

    const container = document.getElementById('topRated');
    if (movies.length === 0) {
        container.innerHTML = '<p>No ratings yet.</p>';
    } else {
        container.innerHTML = `
            <table>
                <thead><tr><th>Title</th><th>Year</th><th>Rating</th><th>Genre</th><th>Director</th></tr></thead>
                <tbody>
                    ${movies.map(m => `
                        <tr>
                            <td>${m.title}</td>
                            <td>${m.release_year}</td>
                            <td>⭐ ${m.average_rating}</td>
                            <td>${m.genre_name || 'N/A'}</td>
                            <td>${m.director_name || 'N/A'}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    }
}

// Load movies by genre
async function loadByGenre() {
    const genre = document.getElementById('genreSelect').value;
    if (!genre) { alert('Select a genre'); return; }
    
    const response = await fetch(`${API_BASE}/genres?genre=${genre}`);
    const movies = await response.json();
    
    const container = document.getElementById('byGenre');
    if (movies.length === 0) {
        container.innerHTML = '<p>No movies in this genre.</p>';
    } else {
        container.innerHTML = `
            <table>
                <thead><tr><th>Title</th><th>Year</th><th>Rating</th></tr></thead>
                <tbody>
                    ${movies.map(m => `
                        <tr>
                            <td>${m.title}</td>
                            <td>${m.release_year}</td>
                            <td>⭐ ${m.average_rating || 0}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    }
}

// Load movies by actor
async function loadByActor() {
    const actor = document.getElementById('actorSelect').value;
    if (!actor) { alert('Select an actor'); return; }
    
    const response = await fetch(`${API_BASE}/actors?action=movies&actor=${actor}`);
    const movies = await response.json();
    
    const container = document.getElementById('byActor');
    if (movies.length === 0) {
        container.innerHTML = '<p>No movies for this actor.</p>';
    } else {
        container.innerHTML = `
            <table>
                <thead><tr><th>Title</th><th>Year</th><th>Rating</th><th>Role</th></tr></thead>
                <tbody>
                    ${movies.map(m => `
                        <tr>
                            <td>${m.title}</td>
                            <td>${m.release_year}</td>
                            <td>⭐ ${m.average_rating || 0}</td>
                            <td>${m.role_name || 'N/A'}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    }
}

// Load all dropdowns
async function loadDropdowns() {
    // Movies
    const moviesRes = await fetch(`${API_BASE}/movies`);
    const movies = await moviesRes.json();
    
    // Actors
    const actorsRes = await fetch(`${API_BASE}/actors`);
    const actors = await actorsRes.json();
    
    // Directors
    const directorsRes = await fetch(`${API_BASE}/directors`);
    const directors = await directorsRes.json();
    
    // Genres
    const genresRes = await fetch(`${API_BASE}/genres`);
    const genres = await genresRes.json();
    
    // Update dropdowns
    updateSelect('genreId', genres, 'genre_id', 'genre_name');
    updateSelect('directorId', directors, 'director_id', 'director_name');
    updateSelect('linkMovieId', movies, 'movie_id', 'title');
    updateSelect('linkActorId', actors, 'actor_id', 'actor_name');
    updateSelect('reviewMovieId', movies, 'movie_id', 'title');
    updateSelect('genreSelect', genres, 'genre_name', 'genre_name');
    updateSelect('actorSelect', actors, 'actor_name', 'actor_name');
}

function updateSelect(elementId, data, valueKey, textKey) {
    const select = document.getElementById(elementId);
    if (select && data) {
        select.innerHTML = '<option value="">Select</option>' + 
            data.map(item => `<option value="${item[valueKey]}">${item[textKey]}</option>`).join('');
    }
}

// Initialize
loadMovies();
loadActors();
loadDirectors();
loadGenres();
loadReviews();
loadDropdowns();