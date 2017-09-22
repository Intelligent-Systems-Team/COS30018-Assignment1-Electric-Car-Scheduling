package assignment.agents;

import java.util.LinkedList;

import assignment.main.AgentInteraction;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

public class Agent_MasterScheduling extends Agent implements AgentInteraction{
	private String LastMessage = "";
	private LinkedList<String> carNameList = new LinkedList<String>();
	
	public Agent_MasterScheduling() {
		registerO2AInterface(AgentInteraction.class, this);
	}
	
	protected void setup() {
		PrintToSystem(getLocalName() + ": has been created");
		addBehaviour(new ReceiveMessageBehaviour()); //Adds message receiver behaviour
	}

	@Override
	public void AddBehaviour(Behaviour b) {
		addBehaviour(b);
	}
	
	@Override
	public String GetLastMessage() {
		return LastMessage;
	}

	@Override
	public void PrintToSystem(String s) {
		System.out.println(s);
		LastMessage = s;
	}
	
	//Behaviour for receiving messages from the cars/stations
	private class ReceiveMessageBehaviour extends CyclicBehaviour{
		
		@Override
		public void action() {
			PrintToSystem(getLocalName() + ": Listening for message");
			ACLMessage message = receive();
			
			if (message != null) {
				PrintToSystem(getLocalName() + ": Received message [\"" + message.getContent() + "\"] from "
						+ message.getSender().getLocalName());
				
				switch(message.getContent()) {
				case "Register me": //Car is registering itself to the master scheduler
					
					String car = message.getSender().getLocalName();
					if (!CarExist(car)){ 
						carNameList.add(car); //Add car to list
						PrintToSystem(getLocalName() + ": " + car + " has been registered");
						
						ACLMessage reply = message.createReply();
						reply.setPerformative(ACLMessage.REQUEST);
						reply.setContent("What is your preference?");
						
						//Send reply
						send(reply); 
						PrintToSystem(getLocalName() + ": Sending response [\"" + reply.getContent() + "\"] to "
								+ message.getAllReceiver().next());
					}
					
					break;
				}				
			}
			
			block();
		}

		/**
		 * Whether the car name exists in the list already
		 * @param car Name of the car
		 */
		private boolean CarExist(String car) {
			for (int i = 0; i < carNameList.size(); i++) {
				if (carNameList.get(i) == car) { return true; }
			}
			
			return false;
		}
		
	}
}
