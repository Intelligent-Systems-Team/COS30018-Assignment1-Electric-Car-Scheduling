package assignment.agents;

import assignment.main.AgentInteraction;
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
	private String name;
	private String LastMessage = "";

	protected void setup() {
		System.out.println("Car Has been Made");
		Object[] args = getArguments();
		name = args.toString();
		ACLMessage registerRequest = new ACLMessage(ACLMessage.REQUEST);
		//TODO fix the hard coded master
		registerRequest.addReceiver(new AID("Master",AID.ISLOCALNAME) );
		registerRequest.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		registerRequest.setContent("register me");
		addBehaviour(new SendMessageBehaviour(this, registerRequest));
	}

	@Override
	public String GetLastMessage() {
		return LastMessage;
	}

	@Override
	public void AddBehaviour(Behaviour b) {
		addBehaviour(b);
	}

	@Override
	public void PrintToSystem(String s) {
		System.out.println(s);
		LastMessage = s;
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
			System.out.println(getLocalName() + ": " + agree.getSender().getName() + " has agreed to the request");
		}

		protected void handleInform(ACLMessage inform) {
			System.out.println(inform.getContent());
		}

		protected void handleRefuse(ACLMessage refuse) {
			System.out.println(getLocalName() + ": " + refuse.getSender().getName() + " refused request");
		}

		protected void handleFailure(ACLMessage failure) {
			if (failure.getSender().equals(myAgent.getAMS())) {
				System.out.println(getLocalName() + ": " + "Responder does not exist");
			} else {
				System.out.println(getLocalName() + ": " + failure.getSender().getName()
						+ " failed to perform the requested action");
			}
		}
	}
}
