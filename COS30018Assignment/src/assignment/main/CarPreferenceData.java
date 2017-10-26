package assignment.main;

import java.util.LinkedList;

/**
 * This class just holds data about the car's name, id, priority, duration,
 * start time and finish time. 
 * The Master Scheduler hold a list of CarPreferenceData
 * 
 * @author Matthew Ward
 *
 */
public class CarPreferenceData {

	public String agentName;
	public int priority;
	public float startTime = 0, finishTime = 0;
	public float durationRequested;
	public int id;

	public CarPreferenceData(String name) {
		agentName = name;
	}

	/**
	 * Updates its priority (for example, if a car has been removed)
	 * 
	 * @param list
	 */
	public void updatePriority(LinkedList<CarPreferenceData> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) == this) {
				priority = i + 1;
			}
		}
	}
}
