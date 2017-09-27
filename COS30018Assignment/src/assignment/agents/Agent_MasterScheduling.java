package assignment.agents;

import java.util.LinkedList;

import assignment.main.*;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

public class Agent_MasterScheduling extends Agent implements AgentInteraction{
	private String LastMessage = "";
	private LinkedList<CarPreferenceData> carNameList = new LinkedList<CarPreferenceData>();
	
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
				
				switch(message.getPerformative()) {
				case ACLMessage.REQUEST: //Car is registering itself to the master scheduler
					
					String car = message.getSender().getLocalName();
					if (!CarExist(car))
					{
						//Add car to list
						if (AddCar(car)) {PrintToSystem(getLocalName() + ": " + car + " has been registered");} 
						//Check If Can accept the car
						//True
						ACLMessage reply = message.createReply();
						if(true)
						{
						PrintToSystem(getLocalName() + ": " + car + " has been registered");
						reply.setPerformative(ACLMessage.AGREE);
						reply.setContent("You Have Succesfull been registered for Charging");
						}
						//False
						else
						{
							PrintToSystem(getLocalName() + ": " + car + " refused ");
							reply.setPerformative(ACLMessage.REFUSE);
							reply.setContent("Can't Schedle you your deviced prefference");
						}
						//Send reply
						send(reply); 
						PrintToSystem(getLocalName() + ": Sending response [\"" + reply.getContent() + "\"] to "
								+ message.getAllReceiver().next());
					}
					else 
					{
						ACLMessage reply = message.createReply();
						reply.setPerformative(ACLMessage.INFORM);
						reply.setContent("Do you what to Change you Perfs? WARRING you will Loss your Priority in que");
						
						//Send reply
						send(reply); 
						PrintToSystem(getLocalName() + ": Sending response [\"" + reply.getContent() + "\"] to "
								+ message.getAllReceiver().next());
					}
					
					break;
				case ACLMessage.CONFIRM:
					//TODO add the how the master updates the car prefs
					PrintToSystem(getLocalName() + ": " + message.getSender().getName() + " Wants to Change Prefs");
					break;
				case ACLMessage.DISCONFIRM:
					PrintToSystem(getLocalName() + ": " + message.getSender().getName() + " Doesn't Want to Change Prefs");
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
				if (carNameList.get(i).agentName.equalsIgnoreCase(car)) { return true; }
			}
			
			return false;
		}
		
		private boolean AddCar(String name) {
			CarPreferenceData c = new CarPreferenceData(name);
			c.priority = carNameList.size()+1;
			carNameList.add(c);
			return true;
		}
		
		/**
		 * Updates car's preferences
		 * @param name
		 */
		private boolean UpdateCar(String name) {
			if (!RemoveCar(name)) {return false;}
			
			
			for (int i = 0; i < carNameList.size(); i++) {
				carNameList.get(i).updatePriority(carNameList); //Updates cars' priorities (Bump up each car's priority)
			}
			
			//(needs more parameters/preferences)
			if (!AddCar(name)) {return false;} //Adds the car with new parameters
			
			return true;
			
		}
		
		private boolean RemoveCar(String name) {
			CarPreferenceData d = null;
			for (int i = 0; i < carNameList.size(); i++) {
				if (carNameList.get(i).agentName.equalsIgnoreCase(name) ) { d = carNameList.get(i); }
			}
			
			return d != null; //Returns true if the car was found and removed
		}
		
	}
}
