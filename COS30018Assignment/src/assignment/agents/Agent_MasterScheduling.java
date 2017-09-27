package assignment.agents;

import java.util.LinkedList;

import assignment.geneticAlgorithm.GA_Control;
import assignment.main.*;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

public class Agent_MasterScheduling extends Agent implements AgentInteraction{
	
	private Control control;
	
	private LinkedList<CarPreferenceData> carNameList = new LinkedList<CarPreferenceData>();
	private GA_Control ga = new GA_Control();
	
	
	public Agent_MasterScheduling() {
		registerO2AInterface(AgentInteraction.class, this);
	}
	
	protected void setup() {
		PrintToSystem(getLocalName() + ": has been created");
		addBehaviour(new ReceiveMessageBehaviour()); //Adds message receiver behaviour
		
		//Sets up the Genetic Algorithm with the list it needs to reference
		PrintToSystem(getLocalName() + ": " + ga.Setup(carNameList)); 
	}

	@Override
	public void AddBehaviour(Behaviour b) {
		addBehaviour(b);
	}

	@Override
	public void PrintToSystem(String s) {
		System.out.println(s);
		control.AddLastMessage(s);
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
				case "register me": //Car is registering itself to the master scheduler
					
					String car = message.getSender().getLocalName();
					if (!CarExist(car)){
						//Add car to list
						if (AddCar(car)) {PrintToSystem(getLocalName() + ": " + car + " has been registered");} 
						
						ACLMessage reply = message.createReply();
						reply.setPerformative(ACLMessage.AGREE);
						reply.setContent("do you want to charge?");
						
						//Send reply
						send(reply); 
						PrintToSystem(getLocalName() + ": Sending response [\"" + reply.getContent() + "\"] to "
								+ message.getAllReceiver().next());
					}
					else 
					{
						ACLMessage reply = message.createReply();
						reply.setPerformative(ACLMessage.REFUSE);
						reply.setContent("you are all ready registered");
						
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

	@Override
	public void RegisterControl(Control c) {
		control = c;
	}
}
