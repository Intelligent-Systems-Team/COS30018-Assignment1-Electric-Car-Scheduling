package assignment.message;

import java.io.Serializable;

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
	public float duration = 0; // The duration the car will take to charge (in hours)
	public float startRequested = 0; // The earliest start time
	public float finishRequired = 0; // Latest Possible finish time requested by the car

	public PrefernceMessage(String Inputname, float Inputduration, float InputstartRequested,
			float InputfinishRequested) {
		id = Integer.parseInt(Inputname);
		name = Inputname;
		duration = Inputduration;
		startRequested = InputstartRequested;
		finishRequired = InputfinishRequested;
	}
}
