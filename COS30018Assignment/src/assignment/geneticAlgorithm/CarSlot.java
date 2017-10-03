package assignment.geneticAlgorithm;

public class CarSlot  implements Comparable<CarSlot> {

	public String name = ""; //Car's local name
	public int priority = -1; //The priority of the car (first car's request is priority 1)
	public float duration = 0; //The duration the car will take to charge (in hours)
	public float startRequested = 0, finishRequired = 0; //The start and finish time requested by the car
	public float startTime = 0; //The actual start time determined by the algorithm
	//startTime in 24 hour time (9.5 = 9:30am | 19.25 = 7:15pm)
	
	
	//*******************************************************************************
	//Below is just used for sorting cars in a schedule to be from earliest to latest
	@Override
	public int compareTo(CarSlot compare) {
		
		float comparedStart = compare.startTime;
		
		if (this.startTime > comparedStart) {
			return 1;
		} else if (this.startTime == startTime) {
			return 0;
		} else {
			return -1;
		}
		
		//Code from:
		//https://www.programcreek.com/2013/01/sort-linkedlist-of-user-defined-objects-in-java/
	}
}
