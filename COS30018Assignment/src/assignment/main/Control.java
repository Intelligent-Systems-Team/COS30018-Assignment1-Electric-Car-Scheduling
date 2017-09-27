package assignment.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jade.content.onto.annotations.Result;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import jade.util.leap.Properties;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.JadeGateway;

public class Control implements ActionListener{

	private JADEController controller;
	private JLabel latestMessages;
	private JButton startJADE, startSimulation, addCar;
	private JPanel simulation;
	private boolean simulating = false;
	private String[] latestMessagesArray = new String[10];
	private ContainerController enviro;
	private int CarNumber;
	
	private void Begin() throws ControllerException, InterruptedException {
		InitializeJadeGateway(); //Sets up Jade Gateway

		//Creates Master Scheduling Agent
		AgentController master = controller.CreateMasterAgent("Master");
				
		//Create Stations
		enviro = controller.CreateContainer("Enviroment");
		controller.CreateContainer("Station 1");
		
		ResetLatestMessagesList();

		//(test) JadeGateway.execute(gmm);
		
		//***Change buttons***
		startSimulation.setText("Stop Simulation!");
		simulation.setVisible(true);
		simulating = true;
		//********************
	}
	//***********************************************************************

	//****************************
	//Control functions/procedures
	//****************************
	public Control(String name) {
		JFrame frame = new JFrame(name);
		Container content = frame.getContentPane();
		content.add(new JLabel(name), BorderLayout.NORTH);
		
		JPanel buttons = new JPanel();
		
		//*******Create two buttons*******
		startJADE = new JButton("Activate JADE controller");
		startJADE.setActionCommand("startJADE");
		startJADE.addActionListener(this);
		
		startSimulation = new JButton("Start Electric Car Scheduling Simulation");
		startSimulation.setActionCommand("startSimulation");
		startSimulation.addActionListener(this);
		startSimulation.setEnabled(false);
		
		addCar = new JButton("Add Car to Simulation");
		addCar.setActionCommand("addCar");
		addCar.addActionListener(this);
		addCar.setEnabled(false);
		//********************************
		
		JPanel display = new JPanel();
		
		buttons.add(startJADE, BorderLayout.WEST);
		buttons.add(startSimulation, BorderLayout.EAST);
		buttons.add(addCar,BorderLayout.NORTH);
		display.add(buttons, BorderLayout.NORTH);
		
		//Display interface
		simulation = new JPanel();
		latestMessages = new JLabel("<html>SystemOut:</html>");
		simulation.add(latestMessages, BorderLayout.SOUTH);
		simulation.setVisible(true);
		display.add(simulation, BorderLayout.SOUTH);
		
		content.add(display, BorderLayout.CENTER);
		frame.pack();
		
		//Frame options
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setBackground(Color.GRAY);
		frame.setVisible(true);
	}
	
	public void AddLastMessage(String newMessage) {
		String displayString = "";
		
		for (int i = 0; i < latestMessagesArray.length-1; i++) {
			latestMessagesArray[i] = latestMessagesArray[i+1]; //Bumps messages up
			displayString += "* " + latestMessagesArray[i] + "<br/>";
		}
		
		latestMessagesArray[latestMessagesArray.length] = newMessage; //Adds latest message
		displayString += latestMessagesArray[latestMessagesArray.length] + "<br/>";
		
		latestMessages.setText("<html>Latest Messages from agents:<br/>\"" + displayString + "\"</html>");
	}
	
	private void ResetLatestMessagesList() {
		for (int i = 0; i < latestMessagesArray.length; i++) {
			latestMessagesArray[i] = " ";
		}
	}
	
	private void InitializeJadeGateway() {
		Properties gatewayProperties = new Properties();
		gatewayProperties.setProperty(Profile.MAIN_HOST, "localhost");
		gatewayProperties.setProperty(Profile.MAIN_PORT, "1099");
		JadeGateway.init(null, gatewayProperties);
		
		System.out.println("Gateway Established");
	}
	
	public AgentInteraction GetInteractionInterface(AgentController a) throws StaleProxyException {
		return a.getO2AInterface(AgentInteraction.class);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("startJADE".equals(e.getActionCommand()) && controller == null){
			System.out.println("StartJADE called");
			try {
				controller = new JADEController(this);
				startJADE.setEnabled(false);
				startSimulation.setEnabled(true);
			} 
			catch (StaleProxyException e1) { e1.printStackTrace();} 
			catch (InterruptedException e1) {e1.printStackTrace();}
		}
		if ("startSimulation".equals(e.getActionCommand()) && controller != null){
			if (!simulating) {
				System.out.println("StartSimulation called");
				try {
					Begin();
					addCar.setEnabled(true);
				} catch (StaleProxyException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ControllerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		if("addCar".equals(e.getActionCommand()) && controller != null)
		{
			try 
			{
				controller.CreatCarAgent(enviro, "Car"+String.valueOf(CarNumber));
				CarNumber++;
			} catch (StaleProxyException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public class TestGetMessages extends Behaviour{
		private ACLMessage masterACLMessage = null;
		private boolean messageReceived = false;
		public String masterMessage = "";
		ACLMessage msg;
		
		private String GetMessage(){
			messageReceived = true;
			return masterMessage;
		}
		
		@Override
		public void action() {
			msg = new ACLMessage(ACLMessage.REQUEST);
			msg.addReceiver(new AID("Master", AID.ISLOCALNAME));
			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
			msg.setContent("What is your latest message?");
			
			//AchieveREInitiator test;
		}
		
		private void updateMasterMessage() {
			try {
				Result result = (Result)myAgent.getContentManager().extractContent(masterACLMessage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean done() {
			return messageReceived;
		}
		
	} 
}
