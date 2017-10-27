package assignment.geneticAlgorithm;

import java.util.LinkedList;
/**
 * This is a class that stores CarSlots and its given Station number.
 * A Schedule will have a list of these StationSlot.
 * @author Matthew Ward 
 *
 */
public class StationSlot {

	public int stationNumber = 0;
	public LinkedList<CarSlot> registeredCars = new LinkedList<CarSlot>();
	
	public StationSlot(int num) {
		stationNumber = num;
	}
	
	public StationSlot Clone() {
		StationSlot clone = new StationSlot(stationNumber);
		
		for (int i = 0; i < registeredCars.size(); i++) {
			clone.registeredCars.add(this.registeredCars.get(i).Clone());
		}
		
		return clone;
	}
}
