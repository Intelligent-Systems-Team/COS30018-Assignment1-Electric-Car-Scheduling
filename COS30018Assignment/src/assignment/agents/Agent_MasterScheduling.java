package assignment.agents;

import java.util.LinkedList;

import assignment.geneticAlgorithm.GA_Control;
import assignment.main.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;

public class Agent_MasterScheduling extends Agent implements AgentInteraction{
	
	private Control control;
	private LinkedList<String> printBuffer = new LinkedList<String>();
	private LinkedList<ACLMessage> messageBuffer = new LinkedList<ACLMessage>();
	
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
		if (control == null) {
			if (s!="") { printBuffer.add(s); }
		} else {
			//Adds any buffered messages first
			for (int count = 0; count < printBuffer.size(); count++) {
				control.AddLastMessage(printBuffer.get(count));
			}
			printBuffer.clear();
			
			//Adds latest message
			if (s!="") { control.AddLastMessage(s); }
		}
	}
	
	//Behaviour for receiving messages from the cars/stations
	private class ReceiveMessageBehaviour extends CyclicBehaviour{
		
		@Override
		public void action() {
			PrintToSystem(getLocalName() + ": Listening for message");
			ACLMessage m = receive();
			
			if (m != null) {
				messageBuffer.add(m); //Stores up messages so it only has to process one at a time
										//Especially if it has to do the genetic algorithm to calculate
										//the current schedule
			}
			
			if (messageBuffer.size() > 0) {
				
				ACLMessage message = messageBuffer.get(0);
				messageBuffer.remove(message);
				
				PrintToSystem(getLocalName() + ": Received message [\"" + message.getContent() + "\"] from "
						+ message.getSender().getLocalName());
				
				switch(message.getPerformative()) {
				case ACLMessage.REQUEST: //Car is registering itself to the master scheduler
					
					
					ACLMessage reply = message.createReply();
					AID sender = (AID) reply.getAllReceiver().next();
					String car = message.getSender().getLocalName();
					
					if (!CarExist(car))
					{
						//Add car to list
						if (AddCar(car)) {PrintToSystem(getLocalName() + ": " + car + " has been registered");} 
												
						//Check If Can accept the car
						//True
						if(true)
						{
						PrintToSystem(getLocalName() + ": " + car + " has been registered");
						reply.setPerformative(ACLMessage.AGREE);
						reply.setContent("you have succesfull been registered for charging");
						}
						//False
						else
						{
						PrintToSystem(getLocalName() + ": " + car + " refused ");
						reply.setPerformative(ACLMessage.REFUSE);
						reply.setContent("can not schedule you or your deviced preference");
						}
						//Send reply
						send(reply);
						PrintToSystem(getLocalName() + ": Sending response [\"" + reply.getContent() + "\"] to "
								+ sender.getLocalName());
					}
					else 
					{
						reply.setPerformative(ACLMessage.INFORM);
						reply.setContent("WARNING changing prefs will lose your priority in que - continue?");
						
						//Send reply
						send(reply); 
						PrintToSystem(getLocalName() + ": Sending response [\"" + reply.getContent() + "\"] to "
								+ sender.getLocalName());
					}
					
					break;
				case ACLMessage.CONFIRM:
					//TODO add the how the master updates the car prefs
					PrintToSystem(getLocalName() + ": " + message.getSender().getName() + " wants to change prefs");
					break;
				case ACLMessage.DISCONFIRM:
					PrintToSystem(getLocalName() + ": " + message.getSender().getName() + " does not want to change prefs");
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
		PrintToSystem("");
	}
}
