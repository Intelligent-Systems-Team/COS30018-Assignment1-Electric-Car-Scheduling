package assignment.ui.TableButton;

//Example from http://www.crionics.com/products/opensource/faq/swing_ex/SwingExamples.html
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import assignment.main.CarType;
import assignment.message.PrefernceMessage;
import assignment.ui.CarsFrame;
import jade.wrapper.ControllerException;

/**
 * @version 1.0 11/09/98
 */

public class SendButtonEditor extends DefaultCellEditor {
	protected JButton button;
	private String label;
	private boolean isPushed;
	
	private CarsFrame carFrame;
	
	private String name; 
	private String type;
	private float start;
	private float finish;
	
	private JTable currentTable;
	private int currentrow; 
	private int currentcolumn;
	

	public SendButtonEditor(JCheckBox checkBox, CarsFrame carframe) {
		super(checkBox);
		carFrame = carframe;
		button = new JButton();
		button.setOpaque(true);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireEditingStopped();
			}
		});
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (isSelected) {
			button.setForeground(table.getSelectionForeground());
			button.setBackground(table.getSelectionBackground());
		} else {
			button.setForeground(table.getForeground());
			button.setBackground(table.getBackground());
		}
		
		//Update currentTable Info
		currentTable = table;
		currentrow =row; 
		currentcolumn = column;
		
		label = (value == null) ? "" : value.toString();
		
		button.setText(label);
		isPushed = true;
		return button;
	}

	public Object getCellEditorValue() {
		if (isPushed) 
		{
			// Get data from Table
			name = (String)currentTable.getValueAt(currentrow, 0);
			type = (String) currentTable.getValueAt(currentrow, 2);
			start = Float.parseFloat((String) currentTable.getValueAt(currentrow, 3));
			finish = Float.parseFloat((String) currentTable.getValueAt(currentrow, 4));
			// Change the Car Status
			currentTable.setValueAt("Sending", currentrow, 1);
			// Pop-up Of Presence Details 
			JOptionPane.showMessageDialog(button, "Sending Data to CarAgent \n"+
					name +":"
					+"\n Start Time Requested:"+start
					+"\n Finish Time Requested:"+finish
					+"\n Car Type:"+type);
			
			SendPrefenceData();
		}
		isPushed = false;
		return new String(label);
	}

	public boolean stopCellEditing() {
		isPushed = false;
		return super.stopCellEditing();
	}

	protected void fireEditingStopped() {
		super.fireEditingStopped();
	}
	
	public void SendPrefenceData()
	{
		PrefernceMessage PMdata = new PrefernceMessage(name,CarType.valueOf(type),start,finish);
		try {
			carFrame.SendCarChargeRequest(PMdata);
		} catch (ControllerException e) {
			e.printStackTrace();
		}
	}
	
}