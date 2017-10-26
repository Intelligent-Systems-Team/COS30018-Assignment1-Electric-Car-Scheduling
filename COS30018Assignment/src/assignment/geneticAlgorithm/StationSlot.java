package assignment.geneticAlgorithm;

import java.util.LinkedList;
/**
 * This is a class That stores CarSlots and the it's given Station number.
 * A Schedule will have a List of these StationSlot.
 * @author Matthew Ward 
 *
 */
public class StationSlot {

	public int stationNumber = 0;
	public LinkedList<CarSlot> registeredCars = new LinkedList<CarSlot>();
	
	public StationSlot(int num) {
		stationNumber = num;
	}
}
