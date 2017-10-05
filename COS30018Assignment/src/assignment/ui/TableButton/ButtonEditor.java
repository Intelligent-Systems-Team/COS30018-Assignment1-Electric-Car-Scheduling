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

/**
 * @version 1.0 11/09/98
 */

public class ButtonEditor extends DefaultCellEditor {
	protected JButton button;

	private String label;

	private boolean isPushed;
	
	private String name; 
	private float duration;
	private float start;
	private float finish;
	
	private JTable currentTable;
	private int currentrow; 
	private int currentcolumn;
	

	public ButtonEditor(JCheckBox checkBox) {
		super(checkBox);
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
			duration = Float.parseFloat((String) currentTable.getValueAt(currentrow, 1));
			start = Float.parseFloat((String) currentTable.getValueAt(currentrow, 2));
			finish = Float.parseFloat((String) currentTable.getValueAt(currentrow, 3));
			//This is where the car will send it's data
			JOptionPane.showMessageDialog(button, "NOT SENDING ONLY TEST"+
					name +":"
					+"\n Start Time Requested:"+start
					+"\n Finish Time Requested:"+finish
					+"\n Duration:"+duration);
			
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
	//TODO Some How figure out how to 
	public void SendPrefenceData()
	{
		
	}
	
}