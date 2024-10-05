const global = {
  currentPath: window.location.pathname,
  fileName: window.location.pathname.split("/").pop(),
  search: {
    type: "",
    term: "",
    page: 1,
    totalPages: 1,
    totalResults: 0,
  },
  api: {
    apikey: "805060ea63e5ef2695e71512c2dbc79e",
    apiUrl: "https://api.themoviedb.org/3/",
  },
};

async function fetchApiData(endpoint) {
  const API_KEY = global.api.apikey;
  const API_URL = global.api.apiUrl;
  showSpinner();
  const response = await fetch(
    `${API_URL}${endpoint}?api_key=${API_KEY}&language=en-US`
  );
  hideSpinner();

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  const data = await response.json();
  return data;
}

async function displayPopularMovies() {
  try {
    const results = await fetchApiData("movie/popular");
    results.results.forEach((movie) => {
      const div = document.createElement("div");
      div.classList.add("card");
      div.innerHTML = `
        <a href="movie-details.html?id=${movie.id}">
          ${
            movie.poster_path
              ? `<img
                  src="https://image.tmdb.org/t/p/w200${movie.poster_path}"
                  class="card-img-top"
                  alt="${movie.title}"
                />`
              : `<img
                  src="/content/dam/movieapp/movies/no-image.jpg"
                  class="card-img-top"
                  alt="${movie.title}"
                />`
          }
        </a>
        <div class="card-body">
          <h5 class="card-title">${movie.title}</h5>
          <p class="card-text">
            <small class="text-muted">Release: ${movie.release_date}</small>
          </p>
        </div>
      `;
      document.querySelector("#popular-movies").appendChild(div);
    });
  } catch (error) {
    console.error("Error fetching popular movies:", error);
  }
}

async function displayPopularShows() {
  try {
    const results = await fetchApiData("tv/popular");
    results.results.forEach((shows) => {
      const div = document.createElement("div");
      div.classList.add("card");
      div.innerHTML = `
        <a href="tv-details.html?id=${shows.id}">
          ${
            shows.backdrop_path
              ? `<img
                  src="https://image.tmdb.org/t/p/w200${shows.backdrop_path}"
                  class="card-img-top"
                  alt="${shows.name}"
                />`
              : `<img
                  src="/content/dam/movieapp/movies/no-image.jpg"
                  class="card-img-top"
                  alt="${shows.name}"
                />`
          }
        </a>
        <div class="card-body">
          <h5 class="card-title">${shows.name}</h5>
          <p class="card-text">
            <small class="text-muted">Release: ${shows.first_air_date}</small>
          </p>
        </div>
      `;
      document.querySelector("#popular-shows").appendChild(div);
    });
  } catch (error) {
    console.error("Error fetching popular movies:", error);
  }
}

async function displayMovieDetails() {
  const movieid = window.location.search.split("=")[1];
  const movie = await fetchApiData(`movie/${movieid}`);

  displayBackgroundImage("movie", movie.backdrop_path);
  const div = document.createElement("div");
  div.innerHTML = `
    <div class="details-top">
      <div>
      ${
        movie.poster_path
          ? `<img
              src="https://image.tmdb.org/t/p/w200${movie.poster_path}"
              class="card-img-top"
              alt="${movie.title}"
            />`
          : `<img
              src="/content/dam/movieapp/movies/no-image.jpg"
              class="card-img-top"
              alt="${movie.title}"
            />`
      }
      </div>
      <div>
        <h2>${movie.title}</h2>
        <p>
          <i class="fas fa-star text-primary"></i>
          ${movie.vote_average.toFixed(1)} / 10
        </p>
        <p class="text-muted">Release Date: ${movie.release_date}</p>
        <p>${movie.overview}</p>
        <h5>Genres</h5>
        <ul class="list-group">
         ${movie.genres.map((genre) => `<li>${genre.name}</li>`).join("")}
        </ul>
        <a href="${
          movie.homepage
        }" target="_blank" class="btn">Visit Movie Homepage</a>
      </div>
    </div>
    <div class="details-bottom">
      <h2>Movie Info</h2>
      <ul>
        <li><span class="text-secondary">Budget:</span> $${movie.budget.toLocaleString()}</li>
        <li><span class="text-secondary">Revenue:</span> $${movie.revenue.toLocaleString()}</li>
        <li><span class="text-secondary">Runtime:</span> ${
          movie.runtime
        } minutes</li>
        <li><span class="text-secondary">Status:</span> ${movie.status}</li>
      </ul>
      <h4>Production Companies</h4>
      <div class="list-group">
        ${movie.production_companies.map((company) => company.name).join(", ")}
      </div>
    </div>
  `;

  document.querySelector("#movie-details").appendChild(div);
}

async function displayShowDetails() {
  const showid = window.location.search.split("=")[1];
  const show = await fetchApiData(`tv/${showid}`);
  console.log(show);

  // Display background image
  displayBackgroundImage("shows", show.backdrop_path);

  // Create a div and fill it with show details
  const div = document.createElement("div");
  div.innerHTML = `
    <div class="details-top">
      <div>
      ${
        show.poster_path
          ? `<img
              src="https://image.tmdb.org/t/p/w200${show.poster_path}"
              class="card-img-top"
              alt="${show.name}"
            />`
          : `<img
              src="/content/dam/movieapp/movies/no-image.jpg"
              class="card-img-top"
              alt="${show.name}"
            />`
      }
      </div>
      <div>
        <h2>${show.name}</h2>
        <p>
          <i class="fas fa-star text-primary"></i>
          ${show.vote_average.toFixed(1)} / 10
        </p>
        <p class="text-muted">Release Date: ${show.release_date}</p>
        <p>
          ${show.overview}
        </p>
        <h5>Genres</h5>
        <ul class="list-group">
          ${show.genres.map((genre) => `<li>${genre.name}</li>`).join("")}
        </ul>
        <a href="${
          show.homepage ? show.homepage : "#"
        }" target="_blank" class="btn" ${
    show.homepage ? "" : 'style="pointer-events:none; opacity:0.5;"'
  }>Visit Show Homepage</a>

      </div>
    </div>
    <div class="details-bottom">
      <h2>Show Info</h2>
      <ul>
        <li><span class="text-secondary">Number Of Episodes:</span> ${
          show.number_of_episodes
        }</li>
        <li><span class="text-secondary">Last Episode To Air:</span> ${
          show.last_episode_to_air ? show.last_episode_to_air.name : "N/A"
        }</li>
        <li><span class="text-secondary">Status:</span> ${show.status}</li>
      </ul>
      <h4>Production Companies</h4>
      <div class="list-group">
        ${show.production_companies
          .map((company) => `<span>${company.name}</span>`)
          .join(", ")}
      </div>
    </div>
  `;

  document.querySelector("#show-details").appendChild(div);
}

async function displaySlider() {
  const { results } = await fetchApiData("movie/now_playing");
  results.forEach((movie) => {
    const div = document.createElement("div");
    div.classList.add("swiper-slide");
    div.innerHTML = `
    <a href="movie-details.html?id=${movie.id}">
            <img
              src="https://image.tmdb.org/t/p/w500${movie.poster_path}"
              alt="${movie.title}"
            />
          </a>
          <h4 class="swiper-rating">
            <i class="fas fa-star text-secondary"></i> ${movie.vote_average} / 10
          </h4>
    `;
    document.querySelector(".swiper-wrapper").appendChild(div);
    initSwipper();
  });
}

async function searchApiData() {
  const API_KEY = global.api.apikey;
  const API_URL = global.api.apiUrl;

  try {
    showSpinner();
    const response = await fetch(
      `${API_URL}search/${global.search.type}?api_key=${API_KEY}&language=en-US&query=${global.search.term}&page=${global.search.page}`
    );
    hideSpinner();

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    console.log("API Data Fetched:", data); // Log the fetched data to the console
    return data;
  } catch (error) {
    console.error("Error fetching search results:", error);
    hideSpinner();
  }
}

async function displaySearch() {
  const queryString = window.location.search;
  const urlData = new URLSearchParams(queryString);
  global.search.type = urlData.get("type");
  global.search.term = urlData.get("search-term");

  if (global.search.term !== "" && global.search.term !== null) {
    const { results, total_pages, page, total_results } = await searchApiData();
    global.search.page = page;
    global.search.totalPages = total_pages;
    global.search.totalResults = total_results;

    if (results.length === 0) {
      showAlert("No results found");
      return;
    }

    displaySearchResuts(results);
  } else {
    showAlert("Please Enter input");
  }
}

function displaySearchResuts(results) {
  document.querySelector("#search-results").innerHTML = "";
  document.querySelector("#search-results-heading").innerHTML = "";
  document.querySelector("#pagination").innerHTML = "";

  results.forEach((result) => {
    const div = document.createElement("div");
    div.classList.add("card");
    div.innerHTML = `
        <a href="${global.search.type}-details.html?id=${result.id}">
          ${
            result.poster_path
              ? `<img
                  src="https://image.tmdb.org/t/p/w200${result.poster_path}"
                  class="card-img-top"
                  alt="${
                    global.search.type === "movie" ? result.title : result.name
                  }"
                />`
              : `<img
                  src="/content/dam/movieapp/movies/no-image.jpg"
                  class="card-img-top"
                  alt="${
                    global.search.type === "movie" ? result.title : result.name
                  }"
                />`
          }
        </a>
        <div class="card-body">
          <h5 class="card-title">${
            global.search.type === "movie" ? result.title : result.name
          }</h5>
          <p class="card-text">
            <small class="text-muted">Release: ${
              global.search.type === "movie"
                ? result.release_date
                : result.first_air_date
            }</small>
          </p>
        </div>
      `;

    document.querySelector("#search-results-heading").innerHTML = `
      <h2>${results.length} of ${global.search.totalResults} Results for "${global.search.term}"</h2>
      `;

    document.querySelector("#search-results").appendChild(div);
  });

  displayPagination();
}

function displayPagination() {
  const div = document.createElement("div");
  div.classList.add("pagination");
  div.innerHTML = `
      <button class="btn btn-primary" id="prev">Prev</button>
      <button class="btn btn-primary" id="next">Next</button>
      <div class="page-counter">Page ${global.search.page} of ${global.search.totalPages}</div>
  `;
  document.querySelector("#pagination").appendChild(div);

  if (global.search.page === 1) {
    document.querySelector("#prev").disabled = true;
  }

  if (global.search.page === global.search.totalPages) {
    document.querySelector("#next").disabled = true;
  }

  document.querySelector("#next").addEventListener("click", async () => {
    global.search.page++;
    const { results, total_pages } = await searchApiData();
    displaySearchResuts(results);
  });

  document.querySelector("#prev").addEventListener("click", async () => {
    global.search.page--;
    const { results, total_pages } = await searchApiData();
    displaySearchResuts(results);
  });
}

function initSwipper() {
  const swiper = new Swiper(".swiper", {
    // Optional parameters
    loop: true, // Makes the slider loop
    slidesPerView: 1, // Number of slides shown at once
    spaceBetween: 10, // Space between slides in pixels

    // If you want autoplay
    autoplay: {
      delay: 3000, // Delay between slides in milliseconds
      disableOnInteraction: false, // Autoplay won't stop after user interaction
    },

    // Responsive breakpoints
    breakpoints: {
      640: {
        slidesPerView: 2, // Show 2 slides on screens 640px wide and up
        spaceBetween: 20, // Space between slides
      },
      768: {
        slidesPerView: 3, // Show 3 slides on screens 768px wide and up
        spaceBetween: 30, // Space between slides
      },
      1024: {
        slidesPerView: 4, // Show 4 slides on screens 1024px wide and up
        spaceBetween: 40, // Space between slides
      },
    },

    // Navigation arrows (optional)
    navigation: {
      nextEl: ".swiper-button-next",
      prevEl: ".swiper-button-prev",
    },

    // Pagination (optional)
    pagination: {
      el: ".swiper-pagination",
      clickable: true,
    },

    // Enable grabbing cursor
    grabCursor: true,
  });
}

function displayBackgroundImage(type, backgroundPath) {
  const overlayDiv = document.createElement("div");
  overlayDiv.style.backgroundImage = `url(https://image.tmdb.org/t/p/original/${backgroundPath})`;
  overlayDiv.style.position = "absolute";
  overlayDiv.style.top = 0;
  overlayDiv.style.left = 0;
  overlayDiv.style.width = "100%";
  overlayDiv.style.height = "100%";
  overlayDiv.style.zIndex = 10; // Higher z-index to ensure it's on top
  overlayDiv.style.backgroundImage = `url('https://image.tmdb.org/t/p/original/${backgroundPath}')`;
  overlayDiv.style.backgroundSize = "cover";
  overlayDiv.style.backgroundPosition = "center";
  overlayDiv.style.backgroundRepeat = "no-repeat";
  overlayDiv.style.opacity = 0.1; // Set the opacity for the overlay

  if (type === "movie") {
    document.querySelector("#movie-details").appendChild(overlayDiv);
  } else {
    document.querySelector("#show-details").appendChild(overlayDiv);
  }
}

function showSpinner() {
  const spinnerElement = document.querySelector(".spinner");
  if (spinnerElement) {
    spinnerElement.classList.add("show");
  }
}

function hideSpinner() {
  const spinnerElement = document.querySelector(".spinner");
  if (spinnerElement) {
    spinnerElement.classList.remove("show");
  }
}

function highlightLinks() {
  const links = document.querySelectorAll(".nav-link");
  links.forEach((link) => {
    if (link.getAttribute("href") === global.currentPath) {
      link.classList.add("active");
    }
  });
}

function showAlert(message, className = "error") {
  const alertEl = document.createElement("div");
  alertEl.classList.add("alert", className);
  alertEl.appendChild(document.createTextNode(message));
  document.querySelector("#alert").appendChild(alertEl);

  setTimeout(() => {
    alertEl.remove();
  }, 3000);
}

function init() {
  switch (global.fileName) {
    case "index.html":
      displaySlider();
      displayPopularMovies();
      break;
    case "movies.html":
      displayPopularMovies();
      break;
    case "tv-shows.html":
      displayPopularShows();
      break;
    case "movie-details.html":
      displayMovieDetails();
      break;
    case "tv-details.html":
      displayShowDetails();
      break;
    case "search.html":
      displaySearch();
      break;
  }
  highlightLinks();
}

document.addEventListener("DOMContentLoaded", init);
