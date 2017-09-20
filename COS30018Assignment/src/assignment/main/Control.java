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

import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Properties;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.JadeGateway;

public class Control implements ActionListener{

	private JADEController controller;
	private JLabel lastMasterMessage, systemOut;
	private JButton startJADE, startSimulation;
	private JPanel simulation;
	private boolean simulating = false;
	private String systemout = "";
	
	private void Begin() throws StaleProxyException {
		InitializeJadeGateway(); //Sets up Jade Gateway

		//Creates Master Scheduling Agent
		controller.CreateMasterAgent(null, "Master");
		
		//Create Stations
		controller.CreateContainer("Station 1");
		
		
		
		//***Change buttons***
		startSimulation.setText("Stop Simulation!");
		LastMasterMessage("");
		simulation.setVisible(true);
		simulating = true;
		//********************
	}
	

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
		//********************************
		
		JPanel display = new JPanel();
		
		buttons.add(startJADE);
		buttons.add(startSimulation, BorderLayout.CENTER);
		display.add(buttons, BorderLayout.NORTH);
		
		//Display interface
		simulation = new JPanel();
		lastMasterMessage = new JLabel("Latest Message from Master:");
		systemout = "SystemOut:";
		systemOut = new JLabel("<html>SystemOut:</html>");
		simulation.add(lastMasterMessage);
		simulation.add(systemOut);
		simulation.setVisible(false);
		display.add(simulation, BorderLayout.SOUTH);
		
		content.add(display, BorderLayout.CENTER);
		frame.pack();
		
		//Frame options
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setBackground(Color.GRAY);
		frame.setVisible(true);
	}
	
	private void SystemOutAdd(String s) {
		systemout += "<br/>" + s;
		systemOut.setText("<html>"+systemout+"</html>");
	}
	
	private void LastMasterMessage(String message) {
		lastMasterMessage.setText("Latest Message from Master: " + message);
	}
	
	private void InitializeJadeGateway() {
		Properties gatewayProperties = new Properties();
		gatewayProperties.setProperty(Profile.MAIN_HOST, "localhost");
		gatewayProperties.setProperty(Profile.MAIN_PORT, "1099");
		JadeGateway.init(null, gatewayProperties);
		
		System.out.println("Gateway Established");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("startJADE".equals(e.getActionCommand()) && controller == null){
			System.out.println("StartJADE called");
			try {
				controller = new JADEController();
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
				} catch (StaleProxyException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	private class GetMastersMessages extends CyclicBehaviour{
		public ACLMessage masterMessage = null;
		ACLMessage msg;
		
		/*
		GetMastersMessages(){
		}
		*/
		
		@Override
		public void action() {
			msg = new ACLMessage(ACLMessage.REQUEST);
			msg.addReceiver(new AID("Master", AID.ISLOCALNAME));
			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
			msg.setContent("What is your latest message?");
		}
		
	} 
}
