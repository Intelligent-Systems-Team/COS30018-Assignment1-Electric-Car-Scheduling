package assignment.agents;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import assignment.main.Control;
import assignment.message.PrefernceMessage;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;

public class Agent_Car extends Agent implements AgentInteraction, CarTableCarAgentIneraction {
	
	private Control control = null;
	private String name;
	private String LastMessage = "";
	private Random rnd = new Random();
	private PrefernceMessage messageContent;
	
	private LinkedList<String> printBuffer = new LinkedList<String>();

	public Agent_Car() {
		registerO2AInterface(AgentInteraction.class, this); //Required to access interface
		registerO2AInterface(CarTableCarAgentIneraction.class, this);
	}
	
	protected void setup() {
		Object[] args = getArguments();
		messageContent = (PrefernceMessage) args[0];
		//SendRegisterRequest(messageContent);
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
	@Override
	public void SendRegisterRequest(PrefernceMessage sendPrefMessage)
	{
		messageContent = sendPrefMessage;
		ACLMessage registerRequest = new ACLMessage(ACLMessage.REQUEST);
		//TODO fix the hard coded master
		registerRequest.addReceiver(new AID("Master",AID.ISLOCALNAME) );
		registerRequest.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		try {
			registerRequest.setContentObject(messageContent);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addBehaviour(new SendMessageBehaviour(this, registerRequest));
	}

	/**
	 * 
	 *
	 *
	 */
	private class SendMessageBehaviour extends AchieveREInitiator {

		public SendMessageBehaviour(Agent a, ACLMessage msg) {
			super(a, msg);
			// TODO Auto-generated constructor stub
		}

		protected void handleAgree(ACLMessage agree) {
			PrintToSystem(getLocalName() + ": " + agree.getSender().getLocalName() + " has agreed to the request");
		}
		
		protected void handleInform(ACLMessage inform) 
		{
			//TODO Propt if you what to keep or Change Preference
			System.out.println(inform.getContent());
			//Choose yes
			ACLMessage message = receive();
			if (message != null) {
				ACLMessage reply = message.createReply();
				if (rnd.nextBoolean()) 
				{
					reply.setPerformative(ACLMessage.CONFIRM);
					reply.setContent("Yes, Please Change");
					try {
						reply.setContentObject(messageContent);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//Choose no
				else
				{
					reply.setPerformative(ACLMessage.DISCONFIRM);
					reply.setContent("No, Don't Change");
				}
				send(reply);
				PrintToSystem(getLocalName() + ": Sending response [\"" + reply.getContent() + "\"] to "
						+ message.getAllReceiver().next());
			}
		}

		protected void handleRefuse(ACLMessage refuse) {
			PrintToSystem(getLocalName() + ": " + refuse.getSender().getLocalName() + " refused request");
		}

		protected void handleFailure(ACLMessage failure) {
			if (failure.getSender().equals(myAgent.getAMS())) {
				PrintToSystem(getLocalName() + ": " + "Responder does not exist");
			} else {
				PrintToSystem(getLocalName() + ": " + failure.getSender().getName()
						+ " failed to perform the requested action");
			}
		}
	}

	@Override
	public void RegisterControl(Control c) {
		control = c;
		PrintToSystem("");
	}

	@Override
	public String AgentName() {
		return getLocalName();
	}
}
