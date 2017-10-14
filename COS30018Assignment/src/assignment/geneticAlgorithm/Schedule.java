package assignment.geneticAlgorithm;

import java.util.Collections;
import java.util.LinkedList;

public class Schedule{

	public LinkedList<StationSlot> stations = new LinkedList<StationSlot>();
	public float fitness = 0;
	
	private int numberOfStations = 0;
	
	public Schedule(int stationNum) {
		numberOfStations = stationNum;
		
		for (int i = 0; i < numberOfStations; i++) {
			stations.add(new StationSlot(i+1));
		}
	}
	
	public CarSlot GetCar(String name, int stationNum) {
		StationSlot station = stations.get(stationNum);
		for(int i = 0; i < station.registeredCars.size(); i++) {
			CarSlot car = station.registeredCars.get(i);
			if (car.name.equalsIgnoreCase(name)) {
				return car;
			}
		}
		
		return null;
	}
	
	public int NumberOfCars() {
		int num = 0;
		
		for (int stationNum = 0; stationNum < numberOfStations; stationNum++) {
			StationSlot station = stations.get(stationNum);
			num += station.registeredCars.size();
		}
		
		return num;
	}
	
	public void OrderCarsByHours() {
		for (int stationNum = 0; stationNum < numberOfStations; stationNum++) {
			StationSlot station = stations.get(stationNum);
			
			int count = station.registeredCars.size()-1;
			while (count > 0) {
				
				for (int i = 0; i < count; i++) {
					CarSlot test1 = station.registeredCars.get(i);
					CarSlot test2 = station.registeredCars.get(i+1);
					
					//Swap
					if (test1.startTime > test2.startTime) {
						CarSlot swap = test1;
						test1 = test2;
						test2 = swap;
					}
					
					station.registeredCars.set(i, test1);
					station.registeredCars.set(i+1, test2);
				}
				
				count--;
			}
		}
	}
	
	public float UnusedHours() {
		float hours = 0;
		
		for (int stationNum = 0; stationNum < numberOfStations; stationNum++) {
			StationSlot station = stations.get(stationNum);
		
			for (int i = 0; i < station.registeredCars.size()-1; i++) {
				CarSlot car1 = station.registeredCars.get(i);
				CarSlot car2 = station.registeredCars.get(i+1);
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
		}
			
		return hours;
	}
	
	public float TotalUnusedHours() 
	{
		float hours = numberOfStations*24;
		
		for (int stationNum = 0; stationNum < numberOfStations; stationNum++) {
			StationSlot station = stations.get(stationNum);
			for (int i = 0; i < station.registeredCars.size()-1; i++) {
				hours -= station.registeredCars.get(i).duration;
			}
		}
		return hours;
	}
	public float PriorityScore() 
	{
		float priorityScore = 0;
		
		for (int stationNum = 0; stationNum < numberOfStations; stationNum++) {
			StationSlot station = stations.get(stationNum);
			for (int i = 0; i < station.registeredCars.size()-1; i++) {
				priorityScore += station.registeredCars.get(i).priority;
			}
		}
		return priorityScore;
	}
	// TODO need to be fixxed. Need to find witch cars are registered
	public float TotalAlloctedTime()
	{
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
	 * Use this to check if a car was able to be slotted into the schedule
	 * @param n - Name of car to search for
	 * @return If car's name was found
	 */
	public boolean CarExist(int id) {
		for (int stationNum = 0; stationNum < numberOfStations; stationNum++) {
			StationSlot station = stations.get(stationNum);
			
			for(int i = 0; i < station.registeredCars.size(); i++) {
				if (station.registeredCars.get(i).id == id) { return true; } 
			}
		}
		
		return false;
	}
	
	public float TimeFromRequested() {
		float hours = 0;
		
		for (int stationNum = 0; stationNum < numberOfStations; stationNum++) {
			StationSlot station = stations.get(stationNum);
			
			for (int i = 0; i < station.registeredCars.size(); i++) {
				CarSlot car = station.registeredCars.get(i);
				
				if (car.startTime >= car.startRequested) {
					hours += (car.startTime-car.startRequested);
				} else {
					System.out.println("error");
				}
				
			}
		}
		
		return hours;
	}
	
}
