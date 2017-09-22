package assignment.main;

import jade.core.behaviours.Behaviour;

public interface AgentInteraction {
	public String GetLastMessage();
	public void AddBehaviour(Behaviour b);
	public void PrintToSystem(String s);
}
