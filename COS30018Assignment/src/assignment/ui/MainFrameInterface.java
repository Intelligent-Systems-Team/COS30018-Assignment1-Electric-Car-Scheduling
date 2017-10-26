package assignment.ui;

import assignment.geneticAlgorithm.Schedule;
import assignment.main.Control;

public interface MainFrameInterface {

	public void UpdateTableSchedule(Schedule schedule);

	public void EnableSimulationButton();

	public void EnableDisplay();

	public void StopDisplay(Control control);

	public void UpdateSystemOut(String string);
	
}
