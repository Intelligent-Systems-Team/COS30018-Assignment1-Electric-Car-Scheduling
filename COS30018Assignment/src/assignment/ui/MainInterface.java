package assignment.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.border.EmptyBorder;

import assignment.geneticAlgorithm.CarSlot;
import assignment.geneticAlgorithm.Schedule;
import assignment.geneticAlgorithm.StationSlot;
import assignment.main.Control;

import javax.swing.JSplitPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;
import java.awt.event.ActionEvent;
import java.awt.SystemColor;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.List;

import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;

public class MainInterface extends JFrame implements MainInterfaceInterface {

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

	/**
	 * Create the frame.
	 */
	public MainInterface(Control controller) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 660, 397);
		contentPane = new JPanel();
		contentPane.setLocation(0, 0);
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

		scrollPane_1 = new JScrollPane();
		splitPane_1.setRightComponent(scrollPane_1);
		
		mySystemOut = new JTextPane();
		mySystemOut.setEditable(false);
		mySystemOut.setText("Latest Messages From Agents:");
		mySystemOut.setEnabled(false);
		scrollPane_1.setViewportView(mySystemOut);

		JSplitPane splitPane_2 = new JSplitPane();
		splitPane_2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_1.setLeftComponent(splitPane_2);

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
		


		JSplitPane splitPane_3 = new JSplitPane();
		splitPane_3.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane_3, BorderLayout.CENTER);

		myLabelSchedule = new JLabel("Current Schedule Fitness: "+ CurrFitness );
		splitPane_3.setLeftComponent(myLabelSchedule);
		
		scrollPane = new JScrollPane();
		scrollPane.setEnabled(false);
		splitPane_3.setBottomComponent(scrollPane);
		
		// Make Table
		Object[][] TabeleData = MakeTableTime(interval);
		table = new JTable();
		dtm = new DefaultTableModel(TabeleData,new String[] {"Time", "Station 1","Station 2","Station 3","Station 4"});
		table.setModel(dtm);
		table.getColumn("Time").setPreferredWidth(30);
		scrollPane.setViewportView(table);
		this.setVisible(true);
	}
	
	private Object[][] MakeTableTime(int interval)
	{
		int hours = 0;
		int minutes = 0;
		ArrayList<Object[]> timeSlots = new ArrayList<Object[]>();
		for( minutes=0; hours < 24; minutes = minutes + interval )
		{
			if(minutes >= 60)
			{
				hours++;
				minutes = minutes - 60;
			}
			Object[] timeSlot = new Object[] {String.format("%02d", hours)+" : "+String.format("%02d",minutes)};
			timeSlots.add(timeSlot);
		}
		
		return (Object[][])timeSlots.toArray(new Object[timeSlots.size()][]);
	}

	@Override
	public void UpdateTableSchedule(Schedule current)
	{
		if(current == null)return;
		CurrFitness = current.fitness;
		float TotalAlloctedTime = current.TotalAlloctedTime();
		float TotalRequestTime = TotalAlloctedTime/CurrFitness;
		// @Debug System.out.println("Fitness: "+CurrFitness+" = "+TotalAlloctedTime + " TotalAlloctedTime / "+TotalRequestTime +"TotalRequestTime");
		/*//Fitness2 Test output
			float TotalunusedHours = current.TotalUnusedHours();
			float PriorityScore = current.PriorityScore();
			float fit = (float) (1/TotalunusedHours)-PriorityScore;
			System.out.println("Fitness: "+fit+", "+TotalunusedHours + " TUsedHours, " + PriorityScore+" Priorty Score");
		*/
		myLabelSchedule.setText("Current Schedule Fitness: "+ CurrFitness);
		for (int station = 1; station <= current.stations.size(); station++) 
		{
			for(int i= 0; i < dtm.getRowCount();i++) 
			{
			dtm.setValueAt("", i, station);
			}
			// System.out.println("TableLength: "+tableLength);
			StationSlot currentStation = current.stations.get(station-1);
			if (currentStation.registeredCars.size() == 0) {
				continue;
			}
			else 
			{
			LinkedList<CarSlot> cars = currentStation.registeredCars;
			for(int i = 0; i < cars.size(); i++)
				{
					CarSlot car = cars.get(i);
					float start = car.startTime;
					float duration = car.duration;
					int rowNum = (int) (start*(60/interval));
					// System.out.println("ColumNum: "+rowNum);
					while(duration > 0) 
					{
						dtm.setValueAt("Car "+car.name+ " P:"+car.priority, rowNum, station);
						rowNum++;
						duration = (float) (duration - interval/60f);
					}
					
				}
			}
		}
	}

	@Override
	public void EnableSimulationButton() {
		btnStartJadeController.setEnabled(false);
		btnStartSimulation.setEnabled(true);
	}

	@Override
	public void EnableDisplay() {
		btnAddCar.setEnabled(true);
		btnStartSimulation.setText("Stop Simulation");
		mySystemOut.setEnabled(true);
		btnClearMessages.setEnabled(true);
		
		//myCurrentSchedule.setEnabled(true);
	}

	@Override
	public void StopDisplay(Control control) {
		btnAddCar.setEnabled(false);
		btnStartSimulation.setText("Start Simulation");
		mySystemOut.setEnabled(false);
		btnClearMessages.setEnabled(false);
		//myCurrentSchedule.setEnabled(false);
		ClearTable();
		control.ResetLatestMessagesList();
	}

	private void ClearTable()
	{
		for (int station = 1; station <= 4; station++) 
		{
			for(int i= 0; i < dtm.getRowCount();i++) 
			{
			dtm.setValueAt("", i, station);
			}
		}
	}

	@Override
	public void UpdateSystemOut(String string) {
		mySystemOut.setText(string);
		JScrollBar vertical = scrollPane_1.getVerticalScrollBar();
		vertical.setValue( vertical.getMaximum() );
	}

}
