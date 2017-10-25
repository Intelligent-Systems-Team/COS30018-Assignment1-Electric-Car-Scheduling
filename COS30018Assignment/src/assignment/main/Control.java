package assignment.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JFrame;

import assignment.agents.AgentInteraction;
import assignment.agents.CarTableCarAgentIneraction;
import assignment.geneticAlgorithm.Schedule;
import assignment.message.PrefernceMessage;
import assignment.ui.CarsInterface;
import assignment.ui.MainInterface;
import assignment.ui.MainInterfaceInterface;
import assignment.ui.DebugMainInterface;
import jade.core.Profile;
import jade.util.leap.Properties;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.JadeGateway;

public class Control implements ActionListener {

	Thread one = new Thread();

	public boolean debug = true; // @Debug

	private JADEController jController;
	public MainInterfaceInterface main;
	private boolean simulating = false;
	private String[] latestMessagesArray = new String[16]; // This number is the number of messages displayed in the UI
	private LinkedList<String> AllMessages = new LinkedList<String>(); // TODO: Not sure if we need to keep track of all
																		// messages?
	private int CarNumber;
	private AgentController master;
	private ContainerController enviro;
	private ContainerController station1;

	private CarsInterface carFrame;
	private Random rnd = new Random();

	public LinkedList<JFrame> carFrames = new LinkedList<JFrame>();

	private void Begin() throws ControllerException, InterruptedException {
		InitializeJadeGateway(); // Sets up Jade Gateway

		// Creates Master Scheduling Agent
		master = jController.CreateMasterAgent("Master");

		// Create Stations
		enviro = jController.CreateContainer("Enviroment");
		// station1 = jController.CreateContainer("Station 1");

		// Make Car GUI
		if (carFrame == null) {
			try {
				carFrame = new CarsInterface(this);
				carFrame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		MakeCarAgentsFormTable();
		UpdateCurrentSchedule(null);
		ResetLatestMessagesList();
		simulating = true;

		// ********************
	}
	// ***********************************************************************

	private void MakeCarAgentsFormTable() 
	{
		for (String carName : carFrame.GetCarIds())
		{
		
		try {
			jController.CreatCarAgent(enviro, carName);
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}

	// ****************************
	// Control functions/procedures
	// ****************************
	public Control(String name) {
		if (debug) {
			main = new DebugMainInterface(this);
		} else {
			main = new MainInterface(this);
		}
	}

	/**
	 * Prints the current schedule to the UI
	 * 
	 * @param current
	 */

	public void NewCarInputs(AgentInteraction car) {
		JFrame carFrame = new JFrame(car.AgentName());

		if (carFrames.size() > 0) {
			carFrame.setLocationRelativeTo(carFrames.getLast());
		} else {
			carFrame.setLocationRelativeTo(null);
		}

		// carFrame.add(new InteractionButton(car, 1, this));

		carFrame.setVisible(true);
		carFrames.add(carFrame);
	}

	public void AddLastMessage(String newMessage) {
		String displayString = "";

		for (int i = 0; i < latestMessagesArray.length - 1; i++) {
			latestMessagesArray[i] = latestMessagesArray[i + 1]; // Bumps messages up
			displayString += "\n* " + latestMessagesArray[i];
		}

		latestMessagesArray[latestMessagesArray.length - 1] = newMessage; // Adds latest message
		displayString += "\n* " + latestMessagesArray[latestMessagesArray.length - 1];

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
		if ("StartJADE".equals(e.getActionCommand()) && jController == null) {
			System.out.println("StartJADE called");
			try {
				jController = new JADEController(this);

				main.EnableSimulationButton();

			} catch (StaleProxyException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

		} else if ("StartSimulation".equals(e.getActionCommand()) && jController != null) {
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
			} else if (simulating) {
				System.out.println("Simulation Stopped");

				// Toggle simulation boolean
				simulating = false;

				// Kill Environment, Stations and thus, Agents
				System.out.println("Closing Agents and Containers");

				try {
					master.kill();
					enviro.kill();
					//station1.kill();
				} catch (StaleProxyException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// Reset Car Number
				// CarNumber = 0;

				// carFrame.dispose();

				// Reset UI
				main.StopDisplay(this);

				UpdateCurrentSchedule(null);

			}
		} else if ("AddCar".equals(e.getActionCommand()) && jController != null) {
			try {
				float randomStart = (float) rnd.nextInt(12);
				String carName = String.valueOf(CarNumber); // This becomes the car's id AND name

				PrefernceMessage InitPrefernceMessage = null;

				if (!debug) {
					InitPrefernceMessage = new PrefernceMessage(carName, 2f, randomStart,
							randomStart + 2 + (float) rnd.nextInt(10));
				} else {
					InitPrefernceMessage = new PrefernceMessage(carName, 2f, 0, 4);
				}

				AgentController newCar = jController.CreatCarAgent(enviro, carName); // Create the
																											// car agent

				CarNumber++;
				carFrame.AddCarToTable(InitPrefernceMessage); // Adds the Car to the Car Table

				// Create jFrame for car with inputs, preferences, etc??

			} catch (StaleProxyException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if ("ClearMessages".equals(e.getActionCommand()) && jController != null) {
			ResetLatestMessagesList();
		}
	}

	public void SendPefernceToCarAgent(PrefernceMessage sendMessage) throws ControllerException {
		// get An Agent Controller for the required car
		// System.out.println("Looking for " + sendMessage.name);
		AgentController carAgent = enviro.getAgent(sendMessage.name);
		if (carAgent != null) {
			// System.out.println("Found: " + carAgent.getName());
			// System.out.println("with Class" + carAgent.getClass().toString());
			try {
				carAgent.getO2AInterface(CarTableCarAgentIneraction.class).SendRegisterRequest(sendMessage);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Making a Car Object didn't work");
			}
		} else {
			System.out.println("That car Don't excit");
		}
	}

	public void UpdateCarStatus(int carID, String status) {
		carFrame.ChangeCarStatus(carID, status);
	}

	public void UpdateCurrentSchedule(Schedule current) {
		main.UpdateTableSchedule(current);
		//carFrame.CheckCarDrop(current);
	}
}
