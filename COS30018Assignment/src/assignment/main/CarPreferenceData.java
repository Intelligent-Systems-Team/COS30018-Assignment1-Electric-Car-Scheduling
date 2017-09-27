package assignment.main;

import java.util.LinkedList;

public class CarPreferenceData {

	public String agentName;
	public int priority;	
	
	public void updatePriority(LinkedList<CarPreferenceData> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) == this) { priority = i+1; }
		}
	}
}
