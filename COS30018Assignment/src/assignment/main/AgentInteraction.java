package assignment.main;

import assignment.agents.Agent_MasterScheduling;
import jade.core.behaviours.Behaviour;

public interface AgentInteraction {
	public void RegisterControl(Control c);
	public void AddBehaviour(Behaviour b);
	public void PrintToSystem(String s);
}
