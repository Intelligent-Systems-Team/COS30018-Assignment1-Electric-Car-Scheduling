package assignment.message;

import java.io.Serializable;

import assignment.main.CarType;

/**
 * This Class just consist of all the details the car Agent needs to send to the
 * master scheduler. Also the UI Frames use this the pass data around as well.
 * 
 * @author Jacques van Niekerk
 * 
 */
public class PrefernceMessage implements Serializable {
	public String name;
	public int id; // The ID of the car
	public CarType type = CarType.values()[0]; // The car type
	public float startRequested = 0; // The earliest start time
	public float finishRequired = 0; // Latest Possible finish time requested by the car
	

	public PrefernceMessage(String Inputname, CarType InputType, float InputstartRequested,
			float InputfinishRequested) {
		id = Integer.parseInt(Inputname);
		name = Inputname;
		type = InputType;
		startRequested = InputstartRequested;
		finishRequired = InputfinishRequested;
	}
}
