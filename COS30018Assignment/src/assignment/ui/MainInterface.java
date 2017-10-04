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
		
		JButton btnStartJadeController = new JButton("Start JADE Controller");
		btnStartJadeController.setActionCommand("startJade");
		btnStartJadeController.addActionListener(controller);
		
		btnStartJadeController.setBackground(SystemColor.info);
		splitPane.setLeftComponent(btnStartJadeController);
		
		JButton btnStartSimulation = new JButton("Start Simulation");
		btnStartSimulation.setActionCommand("startSimulation");
		btnStartSimulation.addActionListener(controller);
		btnStartSimulation.setBackground(SystemColor.info);
		btnStartSimulation.setEnabled(false);
		splitPane.setRightComponent(btnStartSimulation);
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane_1, BorderLayout.WEST);
		
		JTextPane mySystemOut = new JTextPane();
		splitPane_1.setRightComponent(mySystemOut);
		mySystemOut.setText("Latest Messages From Agents:");
		
		JSplitPane splitPane_2 = new JSplitPane();
		splitPane_2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_1.setLeftComponent(splitPane_2);
		
		JButton btnAddCar = new JButton("Add Car");
		btnAddCar.setActionCommand("startCar");
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

}
