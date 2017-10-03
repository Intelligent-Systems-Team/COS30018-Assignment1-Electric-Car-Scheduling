package assignment.agents;

import java.util.LinkedList;
import java.util.Random;

import assignment.main.AgentInteraction;
import assignment.main.Control;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;

public class CarPrototype extends Agent implements AgentInteraction {
	
	private Control control = null;
	private String name;
	private String LastMessage = "";
	private Random rnd = new Random();
	
	private LinkedList<String> printBuffer = new LinkedList<String>();

	public CarPrototype() {
		registerO2AInterface(AgentInteraction.class, this); //Required to access interface
	}
	
	protected void setup() {
		Object[] args = getArguments();
		name = args.toString();
		//addBehaviour(new SendMessageBehaviour(this, registerRequest));
		SendRegisterRequest();
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
	
	// TODO get button to do this
	public void SendRegisterRequest()
	{
		ACLMessage registerRequest = new ACLMessage(ACLMessage.REQUEST);
		//TODO fix the hard coded master
		registerRequest.addReceiver(new AID("Master",AID.ISLOCALNAME) );
		registerRequest.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		registerRequest.setContent("register me");
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
}
