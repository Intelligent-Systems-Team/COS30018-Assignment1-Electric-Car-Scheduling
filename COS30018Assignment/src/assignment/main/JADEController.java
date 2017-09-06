package assignment.main;

import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.*;

public class JADEController {
	private ContainerController mainCtrl;
	private Runtime rt;
	public JADEController() throws StaleProxyException, InterruptedException {
		// Get a hold to the JADE runtime
		rt = Runtime.instance();
		// Launch the Main Container (with the administration GUI on top) listening on
		// port 8888
		System.out.println(JADEController.class.getName() + ": Launching the platform Main Container...");
		Profile pMain = new ProfileImpl(null, 8888, null);
		pMain.setParameter(Profile.GUI, "true");
		mainCtrl = rt.createMainContainer(pMain);
		// Wait for some time
		Thread.sleep(3000);
		// Create and start an agent of class CounterAgent
		System.out.println(JADEController.class.getName() + ": Starting up a Agent/s with Behaviour...");
		//AgentController agentCtrl = mainCtrl.createNewAgent("AMS Dump Behaviour", AMSDumpAgent.class.getName(),
		//agentCtrl.start();
		//AgentController agent1 = mainCtrl.createNewAgent("a1", ReceiverAgent.class.getName(), new Object[0]);
		//AgentController agent2 = mainCtrl.createNewAgent("a2", ResponderAgent.class.getName(), new Object[0]);
		//agent1.start();
		//agent2.start();
		
		Thread.sleep(1000);
		
		// Wait for some time
		Thread.sleep(2000);
		/*
		try {
			// Retrieve O2A interface exposed by the agent to make it activate
			//System.out.println(MyJADEControllerWk4.class.getName() + ": Activating Agent");
			//MyAgentInterface o2a = agent1.getO2AInterface(MyAgentInterface.class);
			//o2a.activate();
			// Wait for some time
			Thread.sleep(20000);
			// Retrieve O2A interface CounterManager2 exposed by the agent to make it
			// de-activate the counter
			//System.out.println(MyJADEControllerWk4.class.getName() + ": Deactivating counter");
			//o2a.deactivate();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		*/
	}
	
	public ContainerController CreateContainer(String name) {
		Profile profile = new ProfileImpl(null, 8888, null);
		profile.setParameter("container-name", name);
		ContainerController newContainer = rt.createAgentContainer(profile);
		return newContainer;
	}
	
	public void AddAgent(String name, Agent agent) throws StaleProxyException {
		mainCtrl.acceptNewAgent(name, agent);
	}
}
