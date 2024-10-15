"use strict";

let map, mapEvent;

class App {
  #workouts = [];
  constructor() {
    this._getPosition();

    // Bind the form submission event
    form.addEventListener("submit", this._newWorkout.bind(this));

    // Bind the change event for the input type
    inputType.addEventListener("change", this._toggleElevationField.bind(this));
  }

  _getPosition() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        this._loadMap.bind(this),
        function () {
          alert("Could not get your position");
        }
      );
    }
  }

  _loadMap(position) {
    const { latitude, longitude } = position.coords;
    map = L.map("map").setView([latitude, longitude], 13);

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution:
        '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    }).addTo(map);

    map.on("click", (mapE) => {
      mapEvent = mapE;
      form.classList.remove("hidden");
      inputDistance.focus();
    });
  }

  _toggleElevationField() {
    // Toggle fields based on the selected type
    if (inputType.value === "running") {
      inputElevation.closest(".form__row").classList.add("form__row--hidden");
      inputCadence.closest(".form__row").classList.remove("form__row--hidden");
    } else if (inputType.value === "cycling") {
      inputElevation
        .closest(".form__row")
        .classList.remove("form__row--hidden");
      inputCadence.closest(".form__row").classList.add("form__row--hidden");
    }
  }

  _validInputs(...inputs) {
    return inputs.every((inp) => Number.isFinite(inp) && inp > 0);
  }

  _newWorkout(e) {
    e.preventDefault();

    const type = inputType.value;
    const distance = +inputDistance.value;
    const duration = +inputDuration.value;
    const { lat, lng } = mapEvent.latlng;
    let workout;

    if (!mapEvent) {
      alert("Please select a location on the map.");
      return;
    }

    // Running workout
    if (type === "running") {
      const cadence = +inputCadence.value;

      if (!this._validInputs(distance, duration, cadence)) {
        alert(
          "Please enter valid numbers for distance, duration, and cadence."
        );
        return;
      }
      workout = new Running([lat, lng], distance, duration, cadence);
    }

    // Cycling workout
    if (type === "cycling") {
      const elevation = +inputElevation.value;

      if (!this._validInputs(distance, duration, elevation)) {
        alert(
          "Please enter valid numbers for distance, duration, and elevation gain."
        );
        return;
      }
      workout = new Cycling([lat, lng], distance, duration, elevation);
    }

    // Add the new workout to the workouts array
    this.#workouts.push(workout);

    // Log the workout for debugging
    console.log(workout);

    // Dynamically set popup content based on workout type
    const workoutType = type.charAt(0).toUpperCase() + type.slice(1);
    const popupContent = `
      ${workoutType} Workout
      <br>
      Distance: ${distance} km
      <br>
      Duration: ${duration} min
    `;

    // Add marker to the map
    L.marker(workout.coords)
      .addTo(map)
      .bindPopup(
        L.popup({
          maxWidth: 250,
          minWidth: 100,
          autoClose: false,
          closeOnClick: false,
          className: type === "running" ? "running-popup" : "cycling-popup",
        })
      )
      .setPopupContent(popupContent)
      .openPopup();

    // Render the workout in the list
    this._renderWorkout(workout);

    // Clear input fields without hiding the form
    this._resetForm();
  }

  _renderWorkout(workout) {
    let html = `
      <li class="workout workout--${workout.type}" data-id="${workout.id}">
        <h2 class="workout__title">${workout.description}</h2>
        <div class="workout__details">
          <span class="workout__icon">${
            workout.type === "running" ? "🏃‍♂️" : "🚴‍♀️"
          }</span>
          <span class="workout__value">${workout.distance}</span>
          <span class="workout__unit">km</span>
        </div>
        <div class="workout__details">
          <span class="workout__icon">⏱</span>
          <span class="workout__value">${workout.duration}</span>
          <span class="workout__unit">min</span>
        </div>`;

    if (workout.type === "running") {
      html += `
        <div class="workout__details">
          <span class="workout__icon">⚡️</span>
          <span class="workout__value">${workout.pace.toFixed(1)}</span>
          <span class="workout__unit">min/km</span>
        </div>
        <div class="workout__details">
          <span class="workout__icon">🦶🏼</span>
          <span class="workout__value">${workout.cadence}</span>
          <span class="workout__unit">spm</span>
        </div>`;
    } else if (workout.type === "cycling") {
      html += `
        <div class="workout__details">
          <span class="workout__icon">⚡️</span>
          <span class="workout__value">${workout.speed.toFixed(1)}</span>
          <span class="workout__unit">km/h</span>
        </div>
        <div class="workout__details">
          <span class="workout__icon">⛰</span>
          <span class="workout__value">${workout.elevationGain}</span>
          <span class="workout__unit">m</span>
        </div>`;
    }

    html += `</li>`;
    form.insertAdjacentHTML("afterend", html);
  }

  _resetForm() {
    // Clear the input fields but keep the form visible
    inputDistance.value =
      inputDuration.value =
      inputCadence.value =
      inputElevation.value =
        "";
  }
}
