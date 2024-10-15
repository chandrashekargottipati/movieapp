class Workout {
  date = new Date();
  id = (Date.now() + "").slice(-10);

  constructor(coords, distance, duration) {
    this.coords = coords; // [latitude, longitude]
    this.distance = distance; // in km
    this.duration = duration; // in minutes
  }

  _formatDate() {
    const options = { month: "long", day: "numeric" };
    return new Intl.DateTimeFormat("en-US", options).format(this.date);
  }
}

class Running extends Workout {
  constructor(coords, distance, duration, cadence) {
    super(coords, distance, duration);
    this.cadence = cadence; // cadence in steps per minute
    this.calcPace();
    this.description = `🏃‍♂️ Running on ${this._formatDate()}`;
  }

  calcPace() {
    this.pace = this.duration / this.distance; // pace in min/km
    return this.pace;
  }
}

class Cycling extends Workout {
  // Changed to 'Cycling'
  constructor(coords, distance, duration, elevationGain) {
    super(coords, distance, duration);
    this.elevationGain = elevationGain; // elevation gain in meters
    this.calSpeed();
    this.description = `🚴‍♀️ Cycling on ${this._formatDate()}`;
  }

  calSpeed() {
    this.speed = this.distance / (this.duration / 60); // speed in km/h
    return this.speed;
  }
}

// Create instances of Running and Cycling
const run1 = new Running([33, -12], 5.2, 24, 178);
const cycling1 = new Cycling([33, -12], 27, 95, 532); // Changed to 'Cycling'

// For debugging
console.log(run1);
console.log(cycling1);
