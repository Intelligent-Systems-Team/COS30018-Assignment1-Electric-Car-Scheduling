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
	
	String name; 
	String duration;
	String start;
	String finish;

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
		//
		name = (String)table.getValueAt(row, 0);
		duration = (String)table.getValueAt(row, 1);
		start = (String)table.getValueAt(row, 2);
		finish = (String)table.getValueAt(row, 3);
		
		label = (value == null) ? "" : value.toString();
		
		button.setText(label);
		isPushed = true;
		return button;
	}

	public Object getCellEditorValue() {
		if (isPushed) 
		{
			//This is where the car will send it's data
			JOptionPane.showMessageDialog(button, name +":"
					+"\n Start Time Requested:"+start
					+"\n Finish Time Requested:"+finish
					+"\n Duration:"+duration);
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
}