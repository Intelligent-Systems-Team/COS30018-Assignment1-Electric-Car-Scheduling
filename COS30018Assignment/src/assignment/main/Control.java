package assignment.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import assignment.agents.AgentInteraction;
import assignment.agents.Agent_MasterScheduling;
import assignment.geneticAlgorithm.CarSlot;
import assignment.geneticAlgorithm.Schedule;
import assignment.message.PrefernceMessage;
import assignment.ui.CarsInterface;
import assignment.ui.MainInterface;
import jade.content.onto.annotations.Result;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.behaviours.OneShotBehaviour;
import jade.util.leap.Properties;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.JadeGateway;

public class Control implements ActionListener{

	private JADEController jController;
	private MainInterface main;
	private boolean simulating = false;
	private String[] latestMessagesArray = new String[16]; //This number is the number of messages displayed in the UI
	private LinkedList<String> AllMessages = new LinkedList<String>(); //TODO: Not sure if we need to keep track of all messages?
	private int CarNumber;
	private AgentController master;
	private ContainerController enviro;
	private ContainerController station1;
	private CarsInterface carFrame;
	private Random rnd = new Random();
	
	public LinkedList<JFrame> carFrames = new LinkedList<JFrame>();
	
	private void Begin() throws ControllerException, InterruptedException {
		InitializeJadeGateway(); //Sets up Jade Gateway

		//Creates Master Scheduling Agent
		master = jController.CreateMasterAgent("Master");
				
		//Create Stations
		enviro = jController.CreateContainer("Enviroment");
		station1 = jController.CreateContainer("Station 1");
		

		//Make Car GUI
		try {
			carFrame = new CarsInterface();
			carFrame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		UpdateCurrentSchedule(null);
		ResetLatestMessagesList();		
		simulating = true;
		//********************
	}
	//***********************************************************************

	//****************************
	//Control functions/procedures
	//****************************
	public Control(String name) {		
		main = new MainInterface(this);
	}
	
	/**
	 * Prints the current schedule to the UI
	 * @param current
	 */
	public void UpdateCurrentSchedule(Schedule current) {
		if (current != null && current.registeredCars.size() > 0) {
			String schedule = "\n Highest Fitness: "+ current.fitness +"\n";
			for (int station = 1; station <= 1; station++) {			
			
				float currentTime = 0;
				String station1 = "\nStation " + station +" ::";
				
				LinkedList<CarSlot> cars = current.registeredCars;
				float start = cars.getFirst().startTime;
				
				//Adds "-" leading to the first car
				float loop = start;
				while (loop > 0) {
					station1 += " -";
					loop -= 0.5;
				}
				
				//Displays cars in schedule
				for (int s = 0; s < cars.size(); s++) {
					CarSlot car = cars.get(s);
					
					int hours = (int) Math.floor(car.startTime);
					int minutes = (int)Math.ceil(60*(car.startTime - hours));
					station1 += " {"+car.name+")[" + hours + ":" + minutes + "]";
					
					loop = car.duration;
					while (loop > 0) {
						station1 += " *";
						loop -= 0.5;
					}
					
					float finish = car.startTime+car.duration;
					hours = (int) Math.floor(finish);
					minutes = (int)Math.ceil(60*(finish - hours));
					station1 += " [" + hours + ":" + minutes + "]";
					
					float next = (s == cars.size()-1)?(24-finish):(cars.get(s+1).startTime-finish);
					while (next > 0) {
						station1 += " -";
						next -= 0.5;
					}
				}
				
				schedule += station1; //Draw up each station's schedule
			}	
			
			main.UpdateCurrentSchedule(schedule);
			
		} else {
			main.UpdateCurrentSchedule("N/A");
		}
	}
	
	public void NewCarInputs(AgentInteraction car) {
		JFrame carFrame = new JFrame(car.AgentName());
		
		if (carFrames.size() > 0) {
			carFrame.setLocationRelativeTo(carFrames.getLast());
		} else {
			carFrame.setLocationRelativeTo(null);
		}
		
		//carFrame.add(new InteractionButton(car, 1, this));
		
		carFrame.setVisible(true);
		carFrames.add(carFrame);
	}
	
	public void AddLastMessage(String newMessage) {
		String displayString = "";
		
		for (int i = 0; i < latestMessagesArray.length-1; i++) {
			latestMessagesArray[i] = latestMessagesArray[i+1]; //Bumps messages up
			displayString += "\n* " + latestMessagesArray[i];
		}
		
		latestMessagesArray[latestMessagesArray.length - 1] = newMessage; //Adds latest message
		displayString += "\n* " + latestMessagesArray[latestMessagesArray.length-1];
		
		main.UpdateSystemOut("Latest Messages from agents:" + displayString);
		AllMessages.add(newMessage);
	}
	
	public void ResetLatestMessagesList() {
		System.out.println("Messages reset");
		for (int i = 0; i < latestMessagesArray.length; i++) {
			latestMessagesArray[i] = " ";
		}
		AddLastMessage("");
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
		System.out.print("Action listener called with: " + e.getActionCommand());
		if ("StartJADE".equals(e.getActionCommand()) && jController == null){
			System.out.println("StartJADE called");
			try {
				jController = new JADEController(this);
				main.EnableSimulationButton();
			} 
			catch (StaleProxyException e1) { e1.printStackTrace();} 
			catch (InterruptedException e1) {e1.printStackTrace();}
			
		} else if ("StartSimulation".equals(e.getActionCommand()) && jController != null){
			if (!simulating) {
				System.out.println("StartSimulation called");
				try {
					Begin();
					main.EnableDisplay();
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
			else if (simulating)
			{
				System.out.println("Simulation Stopped");

				//Toggle simulation boolean
				simulating = false;
				
				//Kill Environment, Stations and thus, Agents
				System.out.println("Closing Agents and Containers");
				
				try {
					master.kill();
					enviro.kill();
					station1.kill();
				} catch (StaleProxyException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				//Reset UI
				main.StopDisplay(this);
				UpdateCurrentSchedule(null);
				
				//Reset Car Number
				CarNumber=0;
			}
		} else if("AddCar".equals(e.getActionCommand()) && jController != null) {
			try 
			{
				float randomStart = (float)rnd.nextInt(12);
				String carName = "Car"+String.valueOf(CarNumber);
				PrefernceMessage InitPrefernceMessage = new PrefernceMessage(carName,2f,randomStart,randomStart+2+(float)rnd.nextInt(10));
				AgentController newCar = jController.CreatCarAgent(enviro, carName,InitPrefernceMessage); //Create the car agent
				
				CarNumber++;
				carFrame.AddCarToTable(InitPrefernceMessage); // Adds the Car to the Car Table
				
				//Create jFrame for car with inputs, preferences, etc??
				
			} catch (StaleProxyException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if("ClearMessages".equals(e.getActionCommand()) && jController != null) {
			ResetLatestMessagesList();
		}
	} 
}
