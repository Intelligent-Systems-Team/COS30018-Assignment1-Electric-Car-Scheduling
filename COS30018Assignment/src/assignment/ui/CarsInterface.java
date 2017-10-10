package assignment.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
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

import assignment.main.Control;
import assignment.message.PrefernceMessage;
import assignment.ui.TableButton.ButtonEditor;
import assignment.ui.TableButton.ButtonRenderer;
import jade.wrapper.ControllerException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CarsInterface extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private Control controller;

	/**
	 * Create the frame.
	 */
	public CarsInterface(Control _controller) 
	{
		setTitle("Car Agent Message Data");
		controller =_controller;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
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
		DefaultTableModel tableModel = new DefaultTableModel(new Object[][] {},
			new String[] {"Car", "Duration", "Start Time Request", "Lastest Finish Time Request","Send Request"})
		{
			 @Override
		        public boolean isCellEditable(int row, int column)
		        {
		            // Makes The Car Column Read only
		            return !(column == 0);
		        }
		};
		table.setModel(tableModel);
		//Setting the "Send" column to look/act like buttons.
		ButtonEditor sendButton = new ButtonEditor(new JCheckBox(),this);
		table.getColumn("Send Request").setCellRenderer((TableCellRenderer) new ButtonRenderer());
		table.getColumn("Send Request").setCellEditor(sendButton);
	}
	
	public void AddCarToTable(PrefernceMessage InitPrefernceMessage)
	{		
		Object[] newdata = {InitPrefernceMessage.name,
							String.valueOf(InitPrefernceMessage.duration),
							String.valueOf(InitPrefernceMessage.startRequested),
							String.valueOf(InitPrefernceMessage.finishRequired),
							"Send"};
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		dtm.addRow(newdata);
	}
	
	public void SendCarChargeRequest(PrefernceMessage sendMessage) throws ControllerException
	{
		controller.SendPefernceToCarAgent(sendMessage);
	}

	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "SwingAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
}
