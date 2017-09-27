package assignment.agents;

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

	protected void setup() {
		PrintToSystem(this.getLocalName() + ": New Car Has Been Made");
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
	public void AddBehaviour(Behaviour b) {
		addBehaviour(b);
	}

	@Override
	public void PrintToSystem(String s) {
		System.out.println(s);
		control.AddLastMessage(s);
	}
	
	private class SendMessageBehaviour extends AchieveREInitiator {

		public SendMessageBehaviour(Agent a, ACLMessage msg) {
			super(a, msg);
			// TODO Auto-generated constructor stub
		}

		protected void handleAgree(ACLMessage agree) {
			PrintToSystem(getLocalName() + ": " + agree.getSender().getName() + " has agreed to the request");
		}

		protected void handleInform(ACLMessage inform) {
			PrintToSystem(inform.getContent());
		}

		protected void handleRefuse(ACLMessage refuse) {
			PrintToSystem(getLocalName() + ": " + refuse.getSender().getName() + " refused request");
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
	}
}
