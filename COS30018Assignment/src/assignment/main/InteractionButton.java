package assignment.main;

import java.awt.event.ActionListener;

import javax.swing.JButton;

public class InteractionButton extends JButton{

	private AgentInteraction myCar;
	private int myPurpose;
	//1 = activate
	//2 = ???
	
	public InteractionButton(AgentInteraction car, int i, ActionListener a) {
		myCar = car;
		myPurpose = i;
		
		this.setActionCommand(purposeToString(myPurpose));
		this.addActionListener(a);
	}
	
	private String purposeToString(int i) {
		switch(i) {
		case 1:
			return "activate";
		default:
			return "null";
		}
		
	}
	
	
	
}
