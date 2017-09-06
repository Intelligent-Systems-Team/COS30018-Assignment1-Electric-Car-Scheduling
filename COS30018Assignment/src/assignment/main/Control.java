package assignment.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jade.core.Agent;
import jade.wrapper.StaleProxyException;

public class Control implements ActionListener{

	private JADEController controller;
	private JLabel numberOfStations;
	private JButton startJADE, startSimulation;
	private JPanel simulation;
	private boolean simulating = false;
	
	public Control(String name) {
		JFrame frame = new JFrame(name);
		Container content = frame.getContentPane();
		content.add(new JLabel(name), BorderLayout.NORTH);
		
		JPanel buttons = new JPanel();
		
		startJADE = new JButton("Activate JADE controller");
		startJADE.setActionCommand("startJADE");
		startJADE.addActionListener(this);
		
		startSimulation = new JButton("Start Electric Car Scheduling Simulation");
		startSimulation.setActionCommand("startSimulation");
		startSimulation.addActionListener(this);
		startSimulation.setEnabled(false);
		
		JPanel display = new JPanel();
		
		buttons.add(startJADE);
		buttons.add(startSimulation, BorderLayout.CENTER);
		display.add(buttons, BorderLayout.NORTH);
		
		simulation = new JPanel();
		numberOfStations = new JLabel("Number of stations = 0");
		simulation.add(numberOfStations);
		simulation.setVisible(false);
		display.add(simulation, BorderLayout.SOUTH);
		
		content.add(display, BorderLayout.CENTER);
		frame.pack();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setBackground(Color.GRAY);
		frame.setVisible(true);
	}

	public void AddAgent(String name, Agent agent) throws StaleProxyException {
		controller.AddAgent(name, agent);
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
				startSimulation.setText("Stop Simulation!");
				numberOfStations.setText("Simulation Data Will Go Here");
				simulation.setVisible(true);
				simulating = true;
				controller.CreateContainer("Station");
			}
		}
	}
}
