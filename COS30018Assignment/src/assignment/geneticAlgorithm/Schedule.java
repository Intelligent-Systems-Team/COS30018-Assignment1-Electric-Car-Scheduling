package assignment.geneticAlgorithm;

import java.util.LinkedList;

public class Schedule implements Comparable<Schedule>{

	public LinkedList<CarSlot> registeredCars;
	public float fitness = 0;
	
	
	public int UnusedHours() {
		// 
		return 0;
	}
	
	/**
	 * Use this to check if a car was able to be slotted into the schedule
	 * @param n - Name of car to search for
	 * @return If car's name was found
	 */
	public boolean CarExist(String n) {
		for(int i = 0; i < registeredCars.size(); i++) {
			if (registeredCars.get(i).name.equalsIgnoreCase(n)) { return true; } 
		}
		
		return false;
	}
	
	//Code from:
	//https://www.programcreek.com/2013/01/sort-linkedlist-of-user-defined-objects-in-java/
	@Override
	public int compareTo(Schedule s) {
		float comparedSize = s.fitness;
		if (this.fitness < comparedSize) {
			return 1;
		} else if (this.fitness == comparedSize) {
			return 0;
		} else {
			return -1;
		}
	}
	
	public int TimeFromRequested() {

		return 0;
	}
	
}
