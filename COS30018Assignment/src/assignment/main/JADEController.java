package assignment.main;

import assignment.agents.Agent_MasterScheduling;
import assignment.message.PrefernceMessage;

import java.util.Random;

import assignment.agents.Agent_Car;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.*;
/**
 * This class is used to make the JADE runtime environment and Construct agents.
 * @author Matthew Ward
 *
 */
public class JADEController {
	
	private ContainerController mainCtrl; //Main Container
	private Runtime rt;
	private Control c;
	
	public JADEController(Control c) throws StaleProxyException, InterruptedException {
		
		this.c = c; //Sets reference to Control
		rt = Runtime.instance(); // The JADE runtime
		
		// Launch the Main Container (with the administration GUI on top) listening on
		// port 8888
		System.out.println(JADEController.class.getName() + ": Launching the platform Main Container...");
		Profile pMain = new ProfileImpl(null, 8888, null);
		pMain.setParameter(Profile.GUI, "true");
		
		mainCtrl = rt.createMainContainer(pMain);
	}
	
	/**
	 * Creates a container with a name
	 * @param name of container in GUI
	 * @return Container Controller
	 */
	public ContainerController CreateContainer(String name) {
		Profile profile = new ProfileImpl(null, 8888, null);
		profile.setParameter("container-name", name);
		ContainerController newContainer = rt.createAgentContainer(profile);
		return newContainer;
	}
	
	/**
	 * Creates the Master Scheduling Agent
	 * @param ctrl
	 * @param name
	 * @return
	 * @throws StaleProxyException
	 */
	public AgentController CreateMasterAgent(String name) throws StaleProxyException {
		AgentController master = mainCtrl.createNewAgent(name, Agent_MasterScheduling.class.getName(), new Object[0]);
		master.start();
		c.GetInteractionInterface(master).RegisterControl(c); //Registers reference to Control on the agent
		
		
		return master;
	}
	
	/**
	 * Creates the Car Agent in a specific container
	 * @param ctrl
	 * @param name
	 * @return
	 * @throws StaleProxyException
	 * @author Jacques Van Niekerk
	 */
	public AgentController CreatCarAgent(ContainerController ctrl, String name) throws StaleProxyException {
		ContainerController ctr = (ctrl!=null)?ctrl:mainCtrl; //If null, create in main container
		
		// Make the Car agent
		AgentController car = ctr.createNewAgent(name, Agent_Car.class.getName(), new Object[] {});
		car.start();
		c.GetInteractionInterface(car).RegisterControl(c); //Registers reference to Control on the agent		
		
		return car;
	}
	
	/**
	 * Adds an agent to a specific container
	 * @param ctrl
	 * @param name
	 * @param agent
	 * @throws StaleProxyException
	 */
	public void AddAgent(ContainerController ctrl, String name, Agent agent) throws StaleProxyException {
		ctrl.acceptNewAgent(name, agent);
	}
}
