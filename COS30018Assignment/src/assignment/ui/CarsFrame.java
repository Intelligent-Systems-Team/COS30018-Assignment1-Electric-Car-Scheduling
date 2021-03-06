package assignment.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import javax.swing.JTable;
import java.awt.Insets;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import assignment.geneticAlgorithm.Schedule;
import assignment.main.CarType;
import assignment.main.Control;
import assignment.message.PrefernceMessage;
import assignment.ui.TableButton.SendButtonEditor;
import assignment.ui.TableButton.SendButtonRenderer;
import jade.wrapper.ControllerException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionListener;

/**
 * Car frame is just a modified JFrame which has all the data of all the
 * Agent_Cars and has the functionality to make the Agent_Car send a Request.
 * 
 * @author Jacques Van Niekerk
 * @see JFrame
 * @see Agent_Car
 */
public class CarsFrame extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private Control controller;
	private JPanel panel;
	private JButton btnSendAllRequests;
	private DefaultTableModel tableModel;

	/**
	 * Create the frame.
	 */
	public CarsFrame(Control _controller) {
		setTitle("Car Agent Message Data");
		controller = _controller;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 1.0, 1.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 2;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);

		table = new JTable();
		table.setCellSelectionEnabled(true);
		scrollPane.setViewportView(table);
		tableModel = new DefaultTableModel(new Object[][] {}, new String[] { "Car", "Status", "Car Type",
				"Start Time Request", "Lastest Finish Time Request", "Send" }) {
			@Override
			public boolean isCellEditable(int row, int column) {
				// Makes The Car Column Read only
				return !(column == 0);
			}
		};
		table.setModel(tableModel);
		AddCarTypeComboBox();
		
		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 2;
		contentPane.add(panel, gbc_panel);

		btnSendAllRequests = new JButton("Send All Requests");
		btnSendAllRequests.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SendAllCarChargeRequest();
			}
		});
		btnSendAllRequests.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		panel.add(btnSendAllRequests);

		// Setting the "Send" column to look/act like buttons.
		SendButtonEditor sendButton = new SendButtonEditor(new JCheckBox(), this);
		table.getColumn("Send").setCellRenderer((TableCellRenderer) new SendButtonRenderer());
		table.getColumn("Send").setCellEditor(sendButton);
	}

	/**
	 * The Control calls this function passing the a PrefernceMessage, which is then
	 * made into an object[], and then adds the data to a row in the table
	 * 
	 * @param InitPrefernceMessage
	 */
	public void AddCarToTable(PrefernceMessage InitPrefernceMessage) {
		Object[] newdata = { InitPrefernceMessage.name, "Nothing Sent", String.valueOf(InitPrefernceMessage.type),
				String.valueOf(InitPrefernceMessage.startRequested),
				String.valueOf(InitPrefernceMessage.finishRequired), "Send" };
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		dtm.addRow(newdata);
	}

	/**
	 * Sends the PrefernceMessage to the controller, which will then get the right
	 * car Agent to send a request to the Master Scheduler Agent.
	 * 
	 * @param sendMessage
	 * @throws ControllerException
	 */
	public void SendCarChargeRequest(PrefernceMessage sendMessage) throws ControllerException {
		if(InputValidation(sendMessage)) 
		{
		controller.SendPefernceToCarAgent(sendMessage);
		}
		else {ChangeCarStatus(sendMessage.id,"Nothing Sent");}
	}

	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "SwingAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
		}
	}

	/**
	 * For the rows of car data it will send all. it will make a PrefernceMessage
	 * and call SendCarChargeRequest() for each one
	 */
	private void SendAllCarChargeRequest() {
		System.out.println("Send All Car Charge Request");
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String name = (String) table.getValueAt(i, 0);
			CarType type = CarType.valueOf((String) table.getValueAt(i, 2));
			float start = Float.parseFloat((String) table.getValueAt(i, 3));
			float finish = Float.parseFloat((String) table.getValueAt(i, 4));
			PrefernceMessage PMdata = new PrefernceMessage(name, type, start, finish);
			try {
				// System.out.println("Sending Request for: " + name);
				
				SendCarChargeRequest(PMdata);

			} catch (ControllerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			table.setValueAt("Sending", i, 1);
		}
	}

	/**
	 * Changes the given Car status of the given carId in the Table
	 * 
	 * @param carID
	 * @param status
	 */
	public void ChangeCarStatus(int carID, String status) {
		table.setValueAt(status, carID, 1);
	}

	/**
	 * Every time a new Schedule is sent to the MainFrame, this is called to check
	 * if there has been any cars that are add or drop, and then Change there Status
	 * appropriately
	 * 
	 * @param current
	 */
	public void CheckCarDrop(Schedule current) {
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			if ((!current.CarExist(i))
					&& (table.getValueAt(i, 1) == "Registered" || table.getValueAt(i, 1) == "Re-Registered")) {
				ChangeCarStatus(i, "Dropped");
			} else if (current.CarExist(i)
					&& (table.getValueAt(i, 1) == "Dropped" || table.getValueAt(i, 1) == "On Waiting List")) {
				ChangeCarStatus(i, "Re-Registered");
			}

		}
	}

	public List<String> GetCarIds() {
		List<String> carids = new ArrayList<String>();
		for (int i = 0; i < table.getRowCount(); i++) {
			carids.add((String) table.getValueAt(i, 0));
		}
		return carids;
	}
	private void AddCarTypeComboBox()
	{
		JComboBox comboBox = new JComboBox();
	    DefaultComboBoxModel model = new DefaultComboBoxModel();
	    for(CarType carT : CarType.values())
	    {
	    	model.addElement(carT.toString());
	    }
	    comboBox.setModel(model);
	    TableColumn CarTypeColumn = table.getColumn("Car Type");
	    CarTypeColumn.setCellEditor(new DefaultCellEditor(comboBox));
	}
	public void ResetCarStatus()
	{
		for(int i = 0; i < table.getRowCount();i++)
		{
			ChangeCarStatus(i,"Nothing Sent");
		}
	}
	
	public boolean InputValidation(PrefernceMessage message)
	{
		if(message.startRequested < 0 || message.finishRequired < 0)
		{
			System.out.println("Start and Finish times can't be Negitive");
			JOptionPane.showMessageDialog(btnSendAllRequests,"Car"+message.name+ 
					": You can't Send Negitive Start or Finish Times");
			return false;
		}
		if(message.startRequested > 24 || message.finishRequired > 24)
		{
			System.out.println("Start and Finish times can't be Greater then 24");
			JOptionPane.showMessageDialog(btnSendAllRequests,"Car"+message.name+ 
					": You can't Send times greater then 24hour Start or Finish Times");
			return false;
		}
		if(message.startRequested >= message.finishRequired)
		{
			System.out.println("Start Time has to be before Finish Time");
			JOptionPane.showMessageDialog(btnSendAllRequests,"Car"+message.name+ 
					": Start Time has to be before Finish Time");
			return false;
		}
		if(message.finishRequired - message.startRequested < controller.calculateDuration(message.type))
		{
			System.out.println("There is not enogh time to charge Car");
			JOptionPane.showMessageDialog(btnSendAllRequests,"Car"+message.name+ 
					": Time space is too small to charge this type of car");
			return false;
		}
		return true;
	}
}
