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

import assignment.ui.TableButton.ButtonEditor;
import assignment.ui.TableButton.ButtonRenderer;

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
	private JButton btnAddCarTest;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CarsInterface frame = new CarsInterface();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CarsInterface() 
	{
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
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		table.setModel(new DefaultTableModel(
			new Object[][] {},
			new String[] {"Car", "Duration", "Start Time Request", "Lastest Finish Time Request","Send Request"}
		));
		table.getColumn("Send Request").setCellRenderer((TableCellRenderer) new ButtonRenderer());
		table.getColumn("Send Request").setCellEditor(new ButtonEditor(new JCheckBox()));
		
		btnAddCarTest = new JButton("Add Car Test");
		btnAddCarTest.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				AddCarToTable();
			}
		});		
		
		GridBagConstraints gbc_btnAddCarTest = new GridBagConstraints();
		gbc_btnAddCarTest.gridx = 0;
		gbc_btnAddCarTest.gridy = 1;
		contentPane.add(btnAddCarTest, gbc_btnAddCarTest);
	}
	public void AddCarToTable()
	{
		JButton sendRequest = new JButton();
		sendRequest.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SendCarChargeRequest();
			}
		});		
		Object[] newdata = {"Carname","Duration","Start","Finish","Send"};
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		dtm.addRow(newdata);
	}
	
	public void SendCarChargeRequest()
	{
		
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
