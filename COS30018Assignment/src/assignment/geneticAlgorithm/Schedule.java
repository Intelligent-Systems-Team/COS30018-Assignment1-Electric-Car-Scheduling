package assignment.geneticAlgorithm;

import java.util.Collections;
import java.util.LinkedList;

public class Schedule implements Comparable<Schedule>{

	public LinkedList<CarSlot> registeredCars = new LinkedList<CarSlot>();
	public float fitness = 0;
	
	public void OrderCarsByHours() {
		int count = registeredCars.size()-1;
		while (count > 0) {
			
			for (int i = 0; i < count; i++) {
				CarSlot test1 = registeredCars.get(i);
				CarSlot test2 = registeredCars.get(i+1);
				
				//Swap
				if (test1.startTime > test2.startTime) {
					CarSlot swap = test1;
					test1 = test2;
					test2 = swap;
				}
				
				registeredCars.set(i, test1);
				registeredCars.set(i+1, test2);
			}
			
			count--;
		}
	}
	
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
