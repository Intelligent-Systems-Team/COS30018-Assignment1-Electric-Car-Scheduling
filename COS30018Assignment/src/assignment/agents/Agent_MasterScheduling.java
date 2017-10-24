package assignment.agents;

import java.util.LinkedList;

import assignment.geneticAlgorithm.GA_Control;
import assignment.main.*;
import assignment.message.PrefernceMessage;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
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
	@SuppressWarnings("serial")
	private class ReceiveMessageBehaviour extends CyclicBehaviour{
		
		@Override
		public void action() {
			PrintToSystem(getLocalName() + ": Listening for message");
			ACLMessage m = blockingReceive();
			
			if (m != null) {
				messageBuffer.add(m); //Stores up messages so it only has to process one at a time
										//Especially if it has to do the genetic algorithm to calculate
										//the current schedule
			}
			
			if (messageBuffer.size() > 0) {
				
				ACLMessage message = messageBuffer.get(0);
				messageBuffer.remove(message);
				
				PrintToSystem(getLocalName() + ": Received message [\"" + message.getProtocol() + "\"] from "
						+ message.getSender().getLocalName());
				
				switch(message.getPerformative()) 
				{
				case ACLMessage.REQUEST: //Car is registering itself to the master scheduler
					
					
					ACLMessage reply = message.createReply();
					AID sender = (AID) reply.getAllReceiver().next();
					PrefernceMessage preferenceMessage = null;
					String car = message.getSender().getLocalName();
					int carID = -1;
					try {
						preferenceMessage = (PrefernceMessage) message.getContentObject();
						carID = preferenceMessage.id;
					} catch (UnreadableException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					if (carID >= 0 && !CarExist(carID))
					{												
						//Check If Can accept the car
						//Add car to list	
						if(AddCar(preferenceMessage))
						{
						PrintToSystem(getLocalName() + ": " + car + " has been registered");
						reply.setPerformative(ACLMessage.AGREE);
						reply.setContent("you have succesfull been registered for charging");
						control.UpdateCarStatus(carID, "Registered");
						}
						//False
						else
						{
						PrintToSystem(getLocalName() + ": " + car + " refused ");
						reply.setPerformative(ACLMessage.REFUSE);
						reply.setContent("can not schedule you or your deviced preference");
						control.UpdateCarStatus(carID, "Refused");
						}
						//Send reply
						send(reply);
						PrintToSystem(getLocalName() + ": Sending response [\"" + reply.getContent() + "\"] to "
								+ sender.getLocalName());
					}
					else 
					{
						reply.setPerformative(ACLMessage.INFORM);
						reply.setContent("WARNING changing prefs will lose your priority in queue - continue?");
						control.UpdateCarStatus(carID, "Updating"); // TODO Change this to a better Status?
						//Send reply
						send(reply); 
						PrintToSystem(getLocalName() + ": Sending response [\"" + reply.getContent() + "\"] to "
								+ sender.getLocalName());
					}
					
					break;
				case ACLMessage.CONFIRM:
					//TODO add the how the master updates the car prefs
					PrintToSystem(getLocalName() + ": " + message.getSender().getName() + " wants to change prefs");
					PrefernceMessage UpdatePrefernceMessage;
					try {
						UpdatePrefernceMessage = (PrefernceMessage) message.getContentObject();
						carID = UpdatePrefernceMessage.id;
						if(UpdateCar(UpdatePrefernceMessage))
							{
								control.UpdateCarStatus(carID, "Registered");
							}
						else {control.UpdateCarStatus(carID, "Refused");}
					} catch (UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						PrintToSystem("Reseved no UpdatePrefernce");
					}
					break;
				case ACLMessage.DISCONFIRM:
					PrintToSystem(getLocalName() + ": " + message.getSender().getName() + " does not want to change prefs");
					break;
				}				
			}
			
			//block();
		}

		/**
		 * Whether the car name exists in the list already
		 * @param car Name of the car
		 */
		private boolean CarExist(int carID) {
			System.out.println("Checking:" + carID);
			for (int i = 0; i < carNameList.size(); i++) {
				System.out.println("carNameList.get("+ i +").id = " + carNameList.get(i).id);
				if (carNameList.get(i).id == carID) { return true; }
			}
			
			return false;
		}
		
		private boolean AddCar(PrefernceMessage preferenceMessage) {
			CarPreferenceData c = new CarPreferenceData(preferenceMessage.name);
			c.id = preferenceMessage.id;
			c.durationRequested = preferenceMessage.duration;
			c.startTime = preferenceMessage.startRequested;
			c.finishTime = preferenceMessage.finishRequired;
			c.priority = carNameList.size()+1;
			// Remove this later, It was just to test.
			PrintToSystem(getLocalName() + ": Adding/Updating "+ preferenceMessage.name + "(" + preferenceMessage.id + ")..."
					+"\n ** [Start Time Requested:"+preferenceMessage.startRequested + "]"
					+"\n ** [Finish Time Requested:"+preferenceMessage.finishRequired + "]"
					+"\n ** [Duration:"+preferenceMessage.duration + "]");
			carNameList.add(c);
			
			ga.Generate();
			while(!ga.scheduleReady)
			{
			}
			return (ga.GetCurrentSchedule().CarExist(c.id));
		}
		
		/**
		 * Updates car's preferences
		 * @param name
		 */
		private boolean UpdateCar(PrefernceMessage prefernceMessage) {
			if (!RemoveCar(prefernceMessage.id)) 
			{
				System.out.println("Failed to Remove car"); 
				return false;
			}
			
			
			for (int i = 0; i < carNameList.size(); i++) 
			{
				carNameList.get(i).updatePriority(carNameList); //Updates cars' priorities (Bump up each car's priority)
			}
			
			//(needs more parameters/preferences)
			if (!AddCar(prefernceMessage)) {return false;} //Adds the car with new parameters
			
			return true;
			
		}
		
		private boolean RemoveCar(int id) {
			CarPreferenceData d = null;
			for(CarPreferenceData cpd: carNameList)
			{
				if(cpd.id == id)
				{
					d = cpd;
				}
			}
			carNameList.remove(d);
			// Remove that car form all schedules in GA
			ga.RemoveCarFromAllSchedules(id);
			
			return d != null; //Returns true if the car was found and removed
		}
		
	}

	@Override
	public void RegisterControl(Control c) {
		control = c;
		PrintToSystem("");
		ga.RegisterControl(c);
	}

	@Override
	public String AgentName() {
		return getLocalName();
	}
}
