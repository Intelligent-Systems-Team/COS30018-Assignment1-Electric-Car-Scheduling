package assignment.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import assignment.main.Control;

import javax.swing.JSplitPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.SystemColor;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class MainInterface extends JFrame {

	private JButton btnStartJadeController, btnStartSimulation, btnAddCar;
	private JTextPane mySystemOut;
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public MainInterface(Control controller) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 660, 397);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JSplitPane splitPane = new JSplitPane();
		contentPane.add(splitPane, BorderLayout.NORTH);
		
		btnStartJadeController = new JButton("Start JADE Controller");
		btnStartJadeController.setActionCommand("StartJADE");
		btnStartJadeController.addActionListener(controller);
		
		btnStartJadeController.setBackground(SystemColor.info);
		splitPane.setLeftComponent(btnStartJadeController);
		
		btnStartSimulation = new JButton("Start Simulation");
		btnStartSimulation.setActionCommand("StartSimulation");
		btnStartSimulation.addActionListener(controller);
		btnStartSimulation.setBackground(SystemColor.info);
		btnStartSimulation.setEnabled(false);
		splitPane.setRightComponent(btnStartSimulation);
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane_1, BorderLayout.WEST);
		
		mySystemOut = new JTextPane();
		splitPane_1.setRightComponent(mySystemOut);
		mySystemOut.setText("Latest Messages From Agents:");
		
		JSplitPane splitPane_2 = new JSplitPane();
		splitPane_2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_1.setLeftComponent(splitPane_2);
		
		btnAddCar = new JButton("Add Car");
		btnAddCar.setActionCommand("AddCar");
		btnAddCar.addActionListener(controller);
		splitPane_2.setLeftComponent(btnAddCar);
		
		JButton btnClearMessages = new JButton("[Clear Messages]");
		splitPane_2.setRightComponent(btnClearMessages);
		
		JSplitPane splitPane_3 = new JSplitPane();
		splitPane_3.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane_3, BorderLayout.CENTER);
		
		JLabel myLabelSchedule = new JLabel("Current Schedule");
		splitPane_3.setLeftComponent(myLabelSchedule);
		
		JTextPane myCurrentSchedule = new JTextPane();
		splitPane_3.setRightComponent(myCurrentSchedule);
		this.setVisible(true);
	}
	
	public void EnableSimulationButton() {
		btnStartJadeController.setEnabled(false);
		btnStartSimulation.setEnabled(true);
	}

	public void EnableDisplay() {
		btnAddCar.setEnabled(true);
		btnStartSimulation.setText("Stop Simulation");
	}
	
	public void StopDisplay() {
		btnAddCar.setEnabled(false);
		btnStartSimulation.setText("Start Simulation");
	}

	public void UpdateSystemOut(String string) {
		mySystemOut.setText(string);
	}

}
