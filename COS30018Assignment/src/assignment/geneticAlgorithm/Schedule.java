package assignment.geneticAlgorithm;

import java.util.Collections;
import java.util.LinkedList;

public class Schedule{

	public LinkedList<CarSlot> registeredCars = new LinkedList<CarSlot>();
	public float fitness = 0;
	
	public CarSlot GetCar(String name) {
		for(int i = 0; i < registeredCars.size(); i++) {
			CarSlot car = registeredCars.get(i);
			if (car.name.equalsIgnoreCase(name)) {
				return car;
			}
		}
		
		return null;
	}
	
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
	
	public float UnusedHours() {
		float hours = 0;
		OrderCarsByHours();
		
		for (int i = 0; i < registeredCars.size()-1; i++) {
			CarSlot car1 = registeredCars.get(i);
			CarSlot car2 = registeredCars.get(i+1);
			float unused = (car2.startTime - (car1.startTime+car1.duration));
			if (unused > 0) {
				hours += unused;
			} else if (unused < 0){
				System.out.println("--DEBUG--");
				System.out.println("car2.startTime = " + car2.startTime);
				System.out.println("car1.startTime = " + car1.startTime);
				System.out.println("car1.duration = " + car1.duration);
				System.out.println(car2.startTime + "-" + "(" + car1.startTime + "+" + car1.duration + ")");
				System.out.println("Whaa?");
				
				hours -= unused;
			}
		}
			
		return hours;
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
	
	public float TimeFromRequested() {
		float hours = 0;
		
		for (int i = 0; i < registeredCars.size(); i++) {
			CarSlot car = registeredCars.get(i);
			
			if (car.startTime >= car.startRequested) {
				hours += (car.startTime-car.startRequested);
			} else {
				System.out.println("error");
			}
			
		}
		
		return hours;
	}
	
}
