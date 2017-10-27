package assignment.geneticAlgorithm;

/**
 * This class stores the data a Schedule and the genetic algorithm need to know
 * about the Car. It also has the Functionality to clone itself.
 * 
 * @author Matthew Ward
 *
 */
public class CarSlot {

	public String name = ""; // Car's local name
	public String type = "unknown";
	public int priority = -1; // The priority of the car (first car's request is priority 1)
	public float duration = 0; // The duration the car will take to charge (in hours)
	public float startRequested = 0, finishRequired = 0; // The start and finish time requested by the car
	public float startTime = 0; // The actual start time determined by the algorithm
	public int id;
	// startTime is in 24 hour time (9.5 = 9:30am | 19.25 = 7:15pm)

	public CarSlot Clone() {
		CarSlot car = new CarSlot();
		car.id = id;
		car.type = type;
		car.name = name;
		car.priority = priority;
		car.duration = duration;
		car.startRequested = startRequested;
		car.startTime = startTime;
		car.finishRequired = finishRequired;

		return car;
	}
}
