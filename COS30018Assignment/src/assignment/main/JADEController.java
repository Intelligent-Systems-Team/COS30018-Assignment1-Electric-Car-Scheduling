package assignment.main;

import assignment.agents.Agent_MasterScheduling;
import assignment.agents.CarPrototype;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.*;

public class JADEController {
	
	private ContainerController mainCtrl; //Main Container
	private Runtime rt;
	
	public JADEController() throws StaleProxyException, InterruptedException {
		
		rt = Runtime.instance(); // The JADE runtime
		
		// Launch the Main Container (with the administration GUI on top) listening on
		// port 8888
		System.out.println(JADEController.class.getName() + ": Launching the platform Main Container...");
		Profile pMain = new ProfileImpl(null, 8888, null);
		pMain.setParameter(Profile.GUI, "true");
		
		mainCtrl = rt.createMainContainer(pMain);
		
		//Thread.sleep(3000);
	}
	
	/**
	 * Returns the 'AgentInteraction' Interface of the Agent, so it can be interacted with by code
	 * @param agent
	 * @return
	 */
	public AgentInteraction GetAgentInterface(Agent agent) {
		AgentInteraction o2a_interface;
		
		//Retrieve O2A interface exposed by the agent
		System.out.println(agent.getName() + ": Agent Interaction Interace Requested");
		o2a_interface = agent.getO2AInterface(AgentInteraction.class);
		
		//o2a.activate();
		//o2a.deactivate();
		
		return o2a_interface;
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
		AgentController a = mainCtrl.createNewAgent(name, Agent_MasterScheduling.class.getName(), new Object[0]);
		a.start();
		return a;
	}
	
	public AgentController CreatCarAgent(ContainerController ctrl, String name) throws StaleProxyException {
		ContainerController c = (ctrl!=null)?ctrl:mainCtrl; //If null, create in main container
		
		AgentController a = c.createNewAgent(name, CarPrototype.class.getName(), new Object[0]);
		a.start();
		//AgentController a = c.createNewAgent(name, /*Agent_MasterScheduling.class.getName()*/, new Object[0]);
		//a.start();
		return a;
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
