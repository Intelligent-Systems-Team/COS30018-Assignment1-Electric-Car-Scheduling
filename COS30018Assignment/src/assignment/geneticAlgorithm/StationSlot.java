package assignment.geneticAlgorithm;

import java.util.LinkedList;

public class StationSlot {

	public int stationNumber = 0;
	public LinkedList<CarSlot> registeredCars = new LinkedList<CarSlot>();
	
	public StationSlot(int num) {
		stationNumber = num;
	}
}
