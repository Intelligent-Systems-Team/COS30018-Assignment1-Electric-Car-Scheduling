package assignment.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JFrame;

import assignment.agents.AgentInteraction;
import assignment.agents.CarTableCarAgentIneraction;
import assignment.geneticAlgorithm.Schedule;
import assignment.message.PrefernceMessage;
import assignment.ui.CarsFrame;
import assignment.ui.MainFrame;
import assignment.ui.MainFrameInterface;
import assignment.ui.DebugMainFrame;
import jade.core.Profile;
import jade.util.leap.Properties;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.JadeGateway;

/**
 * This class is the class that controls all the interactions between the agents
 * and the UI and the JADE controller.
 * 
 * @author Matthew Ward
 * @author Jacques Van Niekerk
 * @author Brendan Pert
 */
public class Control implements ActionListener {
	public boolean debug = true; // @Debug
	public MainFrameInterface mainFrame;

	private JADEController jController;
	private CarsFrame carFrame;
	private boolean simulating = false;
	private LinkedList<String> AllMessages = new LinkedList<String>();
	private static final int MAX_NUM_MESSAGES = 50;
	private int CarNumber;
	private AgentController master;
	private ContainerController enviro;
	// private ContainerController station1;
	private Random rnd = new Random();

	public Control(String name) {
		if (debug) {
			mainFrame = new DebugMainFrame(this);
		} else {
			mainFrame = new MainFrame(this);
		}
	}

	/**
	 * When one of the mainFrames start the simulation this function will be called.
	 * This will initialize the JADE Gateway and create the MasterScheduling Agent
	 * and the environment container and if there's a CarFrame already it will make
	 * all the required car agents
	 * 
	 * @throws ControllerException
	 * @throws InterruptedException
	 */
	private void Begin() throws ControllerException, InterruptedException {
		InitializeJadeGateway(); // Sets up Jade Gateway

		// Creates Master Scheduling Agent
		master = jController.CreateMasterAgent("Master");

		// Create EnviroMent Container
		enviro = jController.CreateContainer("Enviroment");

		// Make Car GUI
		if (carFrame == null) {
			try {
				carFrame = new CarsFrame(this);
				carFrame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		MakeCarAgentsFormTable();
		UpdateCurrentSchedule(null);
		ResetLatestMessagesList();
		simulating = true;
	}

	/**
	 * Create car agents from the CarFrame Table
	 */
	private void MakeCarAgentsFormTable() {
		for (String carName : carFrame.GetCarIds()) {

			try {
				jController.CreatCarAgent(enviro, carName);
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Updates the MainFrame System Message Display with all the system out messages
	 * from agents plus adds the new Message.
	 * 
	 * @param newMessage
	 */
	public synchronized void AddLastMessage(String newMessage) {
		String displayString = "";
		if (AllMessages.size() >= MAX_NUM_MESSAGES) {
			for (int i = 0; i < (MAX_NUM_MESSAGES / 2); i++)
				AllMessages.removeLast();
		}
		AllMessages.add(newMessage);
		try {
			for (String line : AllMessages) {
				displayString += "\n* " + line;
			}
		} catch (ConcurrentModificationException e) {
			System.out.println("AllMessage ERROR");
			e.printStackTrace();
		}
		mainFrame.UpdateSystemOut("Latest Messages from agents:" + displayString);
	}

	public void ResetLatestMessagesList() {
		System.out.println("Messages reset");
		AllMessages.removeAll(AllMessages);
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

	/**
	 * This Method handles all the Events from the buttons being pressed in the
	 * MainFrame.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.print("Action listener called with: " + e.getActionCommand());
		// Starts the JADE Controller
		if ("StartJADE".equals(e.getActionCommand()) && jController == null) {
			System.out.println("StartJADE called");
			try {
				jController = new JADEController(this);

				mainFrame.EnableSimulationButton();

			} catch (StaleProxyException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		} else if ("StartSimulation".equals(e.getActionCommand()) && jController != null) {
			// Start the Simulation
			if (!simulating) {
				System.out.println("StartSimulation called");
				try {
					Begin();

					mainFrame.EnableDisplay();

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
			// Stops the Simulation
			else if (simulating) {
				System.out.println("Simulation Stopped");

				// Toggle simulation boolean
				simulating = false;

				// Kill Environment, Stations and thus, Agents
				System.out.println("Closing Agents and Containers");

				try {
					master.kill();
					enviro.kill();
				} catch (StaleProxyException e1) {
					e1.printStackTrace();
				}

				// Old Stuff to reset CarFrame.
				// CarNumber = 0;
				// carFrame.dispose();

				// Reset UI
				mainFrame.StopDisplay(this);

				UpdateCurrentSchedule(null);

			}
		} else if ("AddCar".equals(e.getActionCommand()) && jController != null) {
			// Adds a Car Agent and a corresponding row to the CarFrame.
			try {
				float randomStart = (float) rnd.nextInt(12);
				String carName = String.valueOf(CarNumber); // This becomes the car's id AND name

				PrefernceMessage InitPrefernceMessage = null;

				if (!debug) {
					InitPrefernceMessage = new PrefernceMessage(carName, RandomCarType(), randomStart,
							randomStart + 2 + (float) rnd.nextInt(10));
				} else {
					InitPrefernceMessage = new PrefernceMessage(carName, RandomCarType(), 0, 12);
				}
				// Create Car Agent
				AgentController newCar = jController.CreatCarAgent(enviro, carName);

				CarNumber++;
				carFrame.AddCarToTable(InitPrefernceMessage); // Adds the Car to the Car Table

			} catch (StaleProxyException e1) {
				e1.printStackTrace();
			}
		} else if ("ClearMessages".equals(e.getActionCommand()) && jController != null) {
			ResetLatestMessagesList();
		}
	}

	private CarType RandomCarType() {
		int size = CarType.values().length;

		int r = rnd.nextInt(size);
		
		return CarType.values()[r];
	}

	/**
	 * This method will make a Car Agent send the passed preference message to the
	 * master scheduler.
	 * 
	 * @param sendMessage
	 * @throws ControllerException
	 */
	public void SendPefernceToCarAgent(PrefernceMessage sendMessage) throws ControllerException {
		AgentController carAgent = enviro.getAgent(sendMessage.name);
		if (carAgent != null) {
			try {
				carAgent.getO2AInterface(CarTableCarAgentIneraction.class).SendRegisterRequest(sendMessage);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Making a Car Object didn't work");
			}
		} else {
			System.out.println("That car Don't excit");
		}
	}

	/**
	 * Give other objects the ability to use the CarFrame's ChangeCarStatus().
	 * 
	 * @param carID
	 * @param status
	 */
	public void UpdateCarStatus(int carID, String status) {
		carFrame.ChangeCarStatus(carID, status);
	}

	public void UpdateCurrentSchedule(Schedule current) {
		mainFrame.UpdateTableSchedule(current);
		if (current != null)
			carFrame.CheckCarDrop(current);
	}
}
