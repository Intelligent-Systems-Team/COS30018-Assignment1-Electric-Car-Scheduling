package assignment.geneticAlgorithm;

import java.util.Collections;
import java.util.LinkedList;

/**
 * This class stores a list of StationsSlots then stores a list of CarSlots. 
 * It also has some functionalities of getting and removing CarSlots. It's also
 * able to return some calculations which is used in the GA 
 * @author Matthew Ward
 * @author Jacques Van Niekerk
 */
public class Schedule {

	public LinkedList<StationSlot> stations = new LinkedList<StationSlot>();
	public float fitness = 0;

	private int numberOfStations = 0;

	public Schedule(int stationNum) {
		numberOfStations = stationNum;

		for (int i = 0; i < numberOfStations; i++) {
			stations.add(new StationSlot(i + 1));
		}
	}
	
	public Schedule Clone() {
		Schedule clone = new Schedule(numberOfStations);
		
		for (int i = 0; i < numberOfStations; i++) {
			clone.stations.set(i, this.stations.get(i).Clone());
		}
		
		return clone;
	}

	public CarSlot GetCar(String name, int stationNum) {
		StationSlot station = stations.get(stationNum);
		for (int i = 0; i < station.registeredCars.size(); i++) {
			CarSlot car = station.registeredCars.get(i);
			if (car.name.equalsIgnoreCase(name)) {
				return car;
			}
		}

		return null;
	}

	public void RemoveCar(int id) {
		for (StationSlot ss : stations) {
			CarSlot removeCar = null;
			for (CarSlot cs : ss.registeredCars) {
				if (cs.id == id) {
					removeCar = cs;
				}
			}
			if (removeCar != null)
				ss.registeredCars.remove(removeCar);
		}
	}

	/**
	 * Checks if the schedule has a car with the passed id.
	 * 
	 * @param id
	 * @return
	 */
	public boolean CarExist(int id) {
		for (int stationNum = 0; stationNum < numberOfStations; stationNum++) {
			StationSlot station = stations.get(stationNum);

			for (int i = 0; i < station.registeredCars.size(); i++) {
				if (station.registeredCars.get(i).id == id) {
					return true;
				}
			}
		}

		return false;
	}

	public int NumberOfCars() {
		int num = 0;

		for (int stationNum = 0; stationNum < numberOfStations; stationNum++) {
			StationSlot station = stations.get(stationNum);
			num += station.registeredCars.size();
		}

		return num;
	}

	/**
	 * Orders all the CarSlots in each StationSlot by Start time, this makes
	 * checking for Clashes much fast and easier
	 */
	public void OrderCarsByHours() {
		for (int stationNum = 0; stationNum < numberOfStations; stationNum++) {
			StationSlot station = stations.get(stationNum);

			int count = station.registeredCars.size() - 1;
			while (count > 0) {

				for (int i = 0; i < count; i++) {
					CarSlot test1 = station.registeredCars.get(i);
					CarSlot test2 = station.registeredCars.get(i + 1);

					// Swap
					if (test1.startTime > test2.startTime) {
						CarSlot swap = test1;
						test1 = test2;
						test2 = swap;
					}

					station.registeredCars.set(i, test1);
					station.registeredCars.set(i + 1, test2);
				}

				count--;
			}
		}
	}

	/**
	 * Calculates the total amount of time in between all scheduled CarSlots and
	 * returns it.
	 * 
	 * @return
	 */
	public float TimeGap() {
		float hours = 0;

		for (int stationNum = 0; stationNum < numberOfStations; stationNum++) {
			StationSlot station = stations.get(stationNum);

			for (int i = 0; i < station.registeredCars.size() - 1; i++) {
				CarSlot car1 = station.registeredCars.get(i);
				CarSlot car2 = station.registeredCars.get(i + 1);
				float unused = (car2.startTime - (car1.startTime + car1.duration));
				if (unused > 0) {
					hours += unused;
				} else if (unused < 0) {
					System.out.println("--DEBUG-- \n Error with time gap");
					hours -= unused;
				}
			}
		}

		return hours;
	}

	/**
	 * Calculates the total amount of unused hours in all the stations
	 * 
	 * @return
	 */
	public float TotalUnusedHours() {
		float hours = numberOfStations * 24;

		for (int stationNum = 0; stationNum < numberOfStations; stationNum++) {
			StationSlot station = stations.get(stationNum);
			for (int i = 0; i < station.registeredCars.size() - 1; i++) {
				hours -= station.registeredCars.get(i).duration;
			}
		}
		return hours;
	}

	/**
	 * Returns a total of all the cars priorities (The lower the number the higher
	 * the priority)
	 * 
	 * @return
	 */
	public float PriorityScore() {
		float priorityScore = 0;

		for (int stationNum = 0; stationNum < numberOfStations; stationNum++) {
			StationSlot station = stations.get(stationNum);
			for (int i = 0; i < station.registeredCars.size() - 1; i++) {
				priorityScore += station.registeredCars.get(i).priority;
			}
		}
		return priorityScore;
	}

	/**
	 * Returns a total of all the cars priorities (The lower the number the higher
	 * the priority) Returns a sum of all the (1 / cars priorities) , That the lower
	 * priority number ( which has highest priority) has more effect than the higher
	 * ones.
	 * 
	 * @return
	 */
	public float Prioritypoints() {
		float score = 0;
		for (int stationNum = 0; stationNum < numberOfStations; stationNum++) {
			StationSlot station = stations.get(stationNum);
			for (int i = 0; i < station.registeredCars.size(); i++) {
				float temp = station.registeredCars.get(i).priority;
				score += (float) (1 / temp);
				// @Debug System.out.println("score: " + score);
				// @Debug System.out.println("temp: " + temp);
			}
		}
		return score;

	}

	/**
	 * Returns a total of all the CarSlots duration in all the StationSlots.
	 * 
	 * @return
	 */
	public float TotalAlloctedTime() {
		float total = 0;
		for (int stationNum = 0; stationNum < numberOfStations; stationNum++) {
			StationSlot station = stations.get(stationNum);
			for (int i = 0; i < station.registeredCars.size(); i++) {
				total += station.registeredCars.get(i).duration;
			}
		}
		return total;
	}

	/**
	 * Returns the sum of all the cars in all the stations, the difference form
	 * Starting time and requested starting time.
	 * 
	 * @return
	 */
	public float TimeFromRequested() {
		float hours = 0;

		for (int stationNum = 0; stationNum < numberOfStations; stationNum++) {
			StationSlot station = stations.get(stationNum);

			for (int i = 0; i < station.registeredCars.size(); i++) {
				CarSlot car = station.registeredCars.get(i);

				if (car.startTime >= car.startRequested) {
					hours += (car.startTime - car.startRequested);
				} else {
					System.out.println("error");
				}

			}
		}

		return hours;
	}

}
