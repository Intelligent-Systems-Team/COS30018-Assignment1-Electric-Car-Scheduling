package assignment.ui;

import assignment.geneticAlgorithm.CarSlot;
import assignment.geneticAlgorithm.Schedule;
import assignment.geneticAlgorithm.StationSlot;
import assignment.main.Control;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.ButtonGroup;
import javax.swing.BoxLayout;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.CardLayout;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.SystemColor;
import java.awt.Font;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

/**
 * Exactly the same as MainFrame but at the bottom there is a bunch Options
 * about the fitness version, mutation chance, amount of Generations, population
 * size. This is used for tests. as it is easier to change these variables.
 * 
 * @author Jacques Van Niekerk
 * @author Matthew Ward
 * @author Brendan Pert
 * @see JFrame
 * @see MainFrame
 * 
 */
public class DebugMainFrame extends JFrame implements MainFrameInterface {

	private JButton btnStartJadeController, btnStartSimulation, btnAddCar, btnClearMessages;
	private JTextPane mySystemOut;
	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JTable table;
	private DefaultTableModel dtm;
	private int interval = 30;
	private JScrollPane scrollPane_1;
	private float CurrFitness = 0;
	private JLabel myLabelSchedule;
	private JSplitPane BottomSplitPane;
	private JSplitPane FitSplitPane;
	private JSplitPane PopSplitPane;
	private JSplitPane GenSplitPane;
	private JSplitPane MutSplitPane;
	private JSplitPane subSplitPane_1;
	private JSplitPane subSplitPane_2;
	private JSplitPane subSplitPane_3;
	private JLabel lblPopulation;
	private JLabel lblGenerations;
	private JLabel lblMutation;
	private JLabel lblFitness;
	public JComboBox fitnessCB;
	public JTextField text_Generations;
	public JTextField text_Mutation;
	public JTextField text_Population;

	/**
	 * Create the frame.
	 */
	public DebugMainFrame(Control controller) {
		// Make Frame
		setTitle("Electric Car Charge Scheduling System -DEBUG MODE");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 871, 458);
		// Make JPanel
		contentPane = new JPanel();
		contentPane.setLocation(0, 0);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		// Make TopPanel
		JSplitPane TopSplitPane = new JSplitPane();
		contentPane.add(TopSplitPane, BorderLayout.NORTH);

		btnStartJadeController = new JButton("Start JADE Controller");
		btnStartJadeController.setActionCommand("StartJADE");
		btnStartJadeController.addActionListener(controller);
		btnStartJadeController.setBackground(SystemColor.info);
		TopSplitPane.setLeftComponent(btnStartJadeController);

		btnStartSimulation = new JButton("Start Simulation");
		btnStartSimulation.setActionCommand("StartSimulation");
		btnStartSimulation.addActionListener(controller);
		btnStartSimulation.setBackground(SystemColor.info);
		btnStartSimulation.setEnabled(false);
		TopSplitPane.setRightComponent(btnStartSimulation);

		// Bottom Debug display
		BottomSplitPane = new JSplitPane();
		BottomSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		contentPane.add(BottomSplitPane, BorderLayout.SOUTH);

		PopSplitPane = new JSplitPane();
		lblPopulation = new JLabel("Population:");
		text_Population = new JTextField();
		text_Population.setText("1000");
		text_Population.setColumns(10);
		PopSplitPane.setLeftComponent(lblPopulation);
		PopSplitPane.setRightComponent(text_Population);

		GenSplitPane = new JSplitPane();
		lblGenerations = new JLabel("Generations:");
		text_Generations = new JTextField();
		text_Generations.setText("10");
		text_Generations.setColumns(10);
		GenSplitPane.setLeftComponent(lblGenerations);
		GenSplitPane.setRightComponent(text_Generations);

		MutSplitPane = new JSplitPane();
		lblMutation = new JLabel("Mutation:");
		text_Mutation = new JTextField();
		text_Mutation.setText("0.7");
		text_Generations.setColumns(10);
		MutSplitPane.setLeftComponent(lblMutation);
		MutSplitPane.setRightComponent(text_Mutation);

		FitSplitPane = new JSplitPane();
		lblFitness = new JLabel("Fitness Ver:");
		String[] fitnessCals = { "Original (V1)", "PriorityScore (V2)", "Priority-Hours (V3)", "V5-Extend (V4)",
				"Allocated/Requested (V5)" };
		fitnessCB = new JComboBox(fitnessCals);
		FitSplitPane.setLeftComponent(lblFitness);
		FitSplitPane.setRightComponent(fitnessCB);

		subSplitPane_1 = new JSplitPane();
		subSplitPane_2 = new JSplitPane();
		subSplitPane_3 = new JSplitPane();

		BottomSplitPane.setLeftComponent(FitSplitPane);
		BottomSplitPane.setRightComponent(subSplitPane_1);
		subSplitPane_1.setLeftComponent(MutSplitPane);
		subSplitPane_1.setRightComponent(subSplitPane_2);
		subSplitPane_2.setLeftComponent(PopSplitPane);
		subSplitPane_2.setRightComponent(GenSplitPane);

		// Make Left Side Display
		JSplitPane LeftSplitPane = new JSplitPane();
		LeftSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(LeftSplitPane, BorderLayout.WEST);

		scrollPane_1 = new JScrollPane();
		LeftSplitPane.setRightComponent(scrollPane_1);

		mySystemOut = new JTextPane();
		mySystemOut.setText("Latest Messages From Agents:");
		mySystemOut.setEnabled(false);
		mySystemOut.setEditable(false);
		scrollPane_1.setViewportView(mySystemOut);

		JSplitPane splitPane_2 = new JSplitPane();
		splitPane_2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		LeftSplitPane.setLeftComponent(splitPane_2);

		btnAddCar = new JButton("Add Car");
		btnAddCar.setEnabled(false);
		btnAddCar.setActionCommand("AddCar");
		btnAddCar.addActionListener(controller);
		splitPane_2.setLeftComponent(btnAddCar);

		btnClearMessages = new JButton("[Clear Messages]");
		btnClearMessages.setActionCommand("ClearMessages");
		btnClearMessages.addActionListener(controller);
		btnClearMessages.setEnabled(false);
		splitPane_2.setRightComponent(btnClearMessages);

		// Making Main Display with Table
		JSplitPane RightSplitPane = new JSplitPane();
		RightSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(RightSplitPane, BorderLayout.CENTER);

		myLabelSchedule = new JLabel("Current Schedule Fitness: " + CurrFitness);
		RightSplitPane.setLeftComponent(myLabelSchedule);

		scrollPane = new JScrollPane();
		RightSplitPane.setBottomComponent(scrollPane);

		// Make Table
		Object[][] TabeleData = MakeTableTime(interval);
		table = new JTable();
		dtm = new DefaultTableModel(TabeleData,
				new String[] { "Time", "Station 1", "Station 2", "Station 3", "Station 4" });
		table.setModel(dtm);
		table.getColumn("Time").setPreferredWidth(30);
		scrollPane.setViewportView(table);
		this.setVisible(true);
	}

	/**
	 * Makes a Table with The First Column is all the times for 0:00 to 24:00
	 * depending on the Time Interval.
	 * 
	 * @param interval
	 * @return
	 */
	private Object[][] MakeTableTime(int interval) {
		int hours = 0;
		int minutes = 0;
		ArrayList<Object[]> timeSlots = new ArrayList<Object[]>();
		for (minutes = 0; hours < 24; minutes = minutes + interval) {
			if (minutes >= 60) {
				hours++;
				minutes = minutes - 60;
			}
			Object[] timeSlot = new Object[] { String.format("%02d", hours) + " : " + String.format("%02d", minutes) };
			timeSlots.add(timeSlot);
		}

		return (Object[][]) timeSlots.toArray(new Object[timeSlots.size()][]);
	}

	/**
	 * The Control calls this function to change the schedule display table to the
	 * current Schedule the GA has made
	 */
	@Override
	public void UpdateTableSchedule(Schedule current) {
		if (current == null)
			return;
		CurrFitness = current.fitness;
		myLabelSchedule.setText("Current Schedule Fitness: " + CurrFitness);
		for (int station = 1; station <= current.stations.size(); station++) {
			for (int i = 0; i < dtm.getRowCount(); i++) {
				dtm.setValueAt("", i, station);
			}
			// @Debug System.out.println("TableLength: "+tableLength);
			StationSlot currentStation = current.stations.get(station - 1);
			if (currentStation.registeredCars.size() == 0) {
				continue;
			} else {
				LinkedList<CarSlot> cars = currentStation.registeredCars;
				for (int i = 0; i < cars.size(); i++) {
					CarSlot car = cars.get(i);
					float start = car.startTime;
					float duration = car.duration;
					int rowNum = (int) (start * (60 / interval));
					// System.out.println("ColumNum: "+rowNum);
					while (duration > 0) {
						dtm.setValueAt("Car " + car.name + " P:" + car.priority, rowNum, station);
						rowNum++;
						duration = (float) (duration - interval / 60f);
					}

				}
			}
		}
	}

	public void EnableSimulationButton() {
		btnStartJadeController.setEnabled(false);
		btnStartSimulation.setEnabled(true);
	}

	public void EnableDisplay() {
		btnAddCar.setEnabled(true);
		btnStartSimulation.setText("Stop Simulation");
		mySystemOut.setEnabled(true);
		btnClearMessages.setEnabled(true);
	}

	public void StopDisplay(Control control) {
		btnAddCar.setEnabled(false);
		btnStartSimulation.setText("Start Simulation");
		mySystemOut.setEnabled(false);
		btnClearMessages.setEnabled(false);
		ClearTable();
		control.ResetLatestMessagesList();
	}

	public void UpdateSystemOut(String string) {
		mySystemOut.setText(string);
		JScrollBar vertical = scrollPane_1.getVerticalScrollBar();
		try {
			vertical.setValue(vertical.getMaximum());
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Scroll Error");
			e.printStackTrace();
		}
	}

	private void ClearTable() {
		for (int station = 1; station <= 4; station++) {
			for (int i = 0; i < dtm.getRowCount(); i++) {
				dtm.setValueAt("", i, station);
			}
		}
	}

}
