package assignment.message;

import java.io.Serializable;

public class PrefernceMessage implements Serializable {
	public String name;
	public float duration = 0; //The duration the car will take to charge (in hours)
	public float startRequested = 0; //The earliest start time 
	public float finishRequired = 0; // finish time (Pick up time) requested by the car
	
	public  PrefernceMessage(String Inputname,float Inputduration,float InputstartRequested,float InputfinishRequested)
	{
		name = Inputname;
		duration = Inputduration;
		startRequested =InputstartRequested;
		finishRequired =InputfinishRequested;
	}
}
