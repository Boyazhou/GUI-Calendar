/*Contents of CalendarProgran.class */

//Import packages
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.event.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class CalendarPanel extends JPanel implements ChangeListener{
	private JLabel lblMonth;
	private JButton btnPrev, btnNext, createBtn, quitBtn;
	private JTable tblCalendar;
	   //static Container pane;
	private DefaultTableModel mtblCalendar; //Table model
	private JScrollPane stblCalendar; //The scrollpane
	private JPanel pnlCalendar;
	private int realYear, realMonth, realDay;
	private Model model;

	public CalendarPanel(Model model){
		//Look and feel
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
		catch (ClassNotFoundException e) {}
		catch (InstantiationException e) {}
		catch (IllegalAccessException e) {}
		catch (UnsupportedLookAndFeelException e) {}
		
		this.model = model;
		
		//setSize(330, 375);
		setLayout(new BorderLayout());
		//Create controls
		lblMonth = new JLabel ("January");
		
		//cmbYear = new JComboBox();
		btnPrev = new JButton ("<");
		btnNext = new JButton (">");
		createBtn = new JButton("CREATE");
		quitBtn = new JButton("QUIT");
		
		
		JPanel ctrlPan = new JPanel();
		ctrlPan.add(createBtn);
		ctrlPan.add(quitBtn);
		createBtn.setBounds(0, 0, 20, 20);
		mtblCalendar = new DefaultTableModel(){public boolean isCellEditable(int rowIndex, int mColIndex){return false;}};
		
		tblCalendar = new JTable(mtblCalendar);
		stblCalendar = new JScrollPane(tblCalendar);
		pnlCalendar = new JPanel(null);
		tblCalendar.setShowGrid(false);
		stblCalendar.getVerticalScrollBar().setPreferredSize(new Dimension(0,0));
		
		//Register action listeners
		btnPrev.addActionListener(new btnPrev_Action());
		btnNext.addActionListener(new btnNext_Action());
		createBtn.addActionListener(new create_Action());
		quitBtn.addActionListener(new quit_Action());
		//add mouse listener
		tblCalendar.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				int row = tblCalendar.rowAtPoint(e.getPoint());
				int col= tblCalendar.columnAtPoint(e.getPoint());
				//JOptionPane.showMessageDialog(null,¡± Value in the cell clicked :¡±+ ¡± ¡± +table.getValueAt(row,col).toString());
				model.setDate((int)tblCalendar.getValueAt(row,col));
			}
		});
		
		//Add controls to pane
		add(ctrlPan, BorderLayout.NORTH);
		add(pnlCalendar, BorderLayout.CENTER);
		pnlCalendar.add(lblMonth);
		pnlCalendar.add(btnPrev);
		pnlCalendar.add(btnNext);
		pnlCalendar.add(stblCalendar);
	
		
		//Set bounds
		pnlCalendar.setBounds(0, 0, 320, 335);
		/*setBounds(0, 0, 320, 335);*/
		
	//	lblMonth.setBounds(160-lblMonth.getPreferredSize().width/2, 25, 100, 25);
		lblMonth.setBounds(10, 25, 20, 25);
	
		btnPrev.setBounds(200, 25, 50, 25);
		btnNext.setBounds(260, 25, 50, 25);
		stblCalendar.setBounds(10, 50, 300, 250);
		
		//Get real month/year
		realDay = model.getRealTime().get(GregorianCalendar.DAY_OF_MONTH); //Get day
		realMonth = model.getRealTime().get(GregorianCalendar.MONTH); //Get month
		realYear = model.getRealTime().get(GregorianCalendar.YEAR); //Get year
		
		
		
		//Add headers
		String[] headers = {"S", "M", "T", "W", "T", "F", "S"}; //All headers
		for (int i=0; i<7; i++){
			mtblCalendar.addColumn(headers[i]);
		}
		
		tblCalendar.getParent().setBackground(tblCalendar.getBackground()); //Set background

		//No resize/reorder
		tblCalendar.getTableHeader().setResizingAllowed(false);
		tblCalendar.getTableHeader().setReorderingAllowed(false);

		//Single cell selection
		tblCalendar.setColumnSelectionAllowed(true);
		tblCalendar.setRowSelectionAllowed(true);
		tblCalendar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		//Set row/column count
		tblCalendar.setRowHeight(38);
		mtblCalendar.setColumnCount(7);
		mtblCalendar.setRowCount(6);
		
		//Refresh calendar
		refreshCalendar (realMonth, realYear); //Refresh calendar
	}
	
	public void refreshCalendar(int month, int year){
		//Variables
		String[] months =  {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
		int nod, som; //Number Of Days, Start Of Month
			
		//Allow/disallow buttons
		btnPrev.setEnabled(true);
		btnNext.setEnabled(true);
	
		lblMonth.setText(months[month] + "  " + String.valueOf(year)); //Refresh the month label (at the top)
		lblMonth.setBounds(10, 25, 180, 25);
		//Clear table
		for (int i=0; i<6; i++){
			for (int j=0; j<7; j++){
				mtblCalendar.setValueAt(null, i, j);
			}
		}
		
		//Get first day of month and number of days
		GregorianCalendar cal = new GregorianCalendar(year, month, 1);
		nod = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		som = cal.get(GregorianCalendar.DAY_OF_WEEK);
		
		//Draw calendar
		for (int i=1; i<=nod; i++){
			int row = new Integer((i+som-2)/7);
			int column  =  (i+som-2)%7;
			mtblCalendar.setValueAt(i, row, column);
		}

		//Apply renderers
		tblCalendar.setDefaultRenderer(tblCalendar.getColumnClass(0), new tblCalendarRenderer());
	}
	
	/**
	 * serves as view
	 * @param e
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		refreshCalendar(model.getPrintCal().get(GregorianCalendar.MONTH), model.getPrintCal().get(GregorianCalendar.YEAR));
	}
	
	/**
	 *table renderer
	 */
	class tblCalendarRenderer extends DefaultTableCellRenderer{
		public Component getTableCellRendererComponent (JTable table, Object value, boolean selected, boolean focused, int row, int column){
			super.getTableCellRendererComponent(table, value, selected, focused, row, column);
			setHorizontalAlignment( JLabel.CENTER );
			setBackground(new Color(255, 255, 255));
			
			if (value != null){
				if (Integer.parseInt(value.toString()) == realDay && model.getPrintCal().get(GregorianCalendar.MONTH)== realMonth && model.getPrintCal().get(GregorianCalendar.YEAR) == realYear){ //Today
					setBackground(new Color(220, 220, 255));
					setFont(getFont().deriveFont(Font.BOLD));
				}
			}
			if (value != null){
				if (Integer.parseInt(value.toString()) == model.getPrintCal().get(GregorianCalendar.DAY_OF_MONTH)){ 
					setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
				}
			}
			setForeground(Color.black);
			return this;  
		}
	}
	/*
	 * Action listener: serves as controller
	 */
    class btnPrev_Action implements ActionListener{
		public void actionPerformed (ActionEvent e){
			model.preDay();
		}
	}
    
    class btnNext_Action implements ActionListener{
		public void actionPerformed (ActionEvent e){
			model.nextDay();
		}
	}	
    
    class create_Action implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {   
	       //create the input dialog
			JTextField eventTitle = new JTextField("Untitled event");
	        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");      
	        JTextField date = new JTextField(df.format(model.getPrintCal().getTime()));
	        JTextField startTime = new JTextField("HH:mm (In 24h Format)");
	        JLabel label = new JLabel("TO");
	        JTextField endTime = new JTextField("HH:mm (Put NA if not apply)");
	        
	        JPanel panel = new JPanel(new BorderLayout());
	        panel.add(eventTitle, BorderLayout.NORTH);
	        panel.add(date, BorderLayout.CENTER);
	        
	        JPanel southPanel = new JPanel(new GridLayout(1, 3));
	        southPanel.add(startTime);
	        southPanel.add(label);
	        label.setHorizontalAlignment(JLabel.CENTER);
	        southPanel.add(endTime);
	        panel.add(southPanel, BorderLayout.SOUTH);
	    
	        
	        int result = JOptionPane.showConfirmDialog(null, panel, "CREATE EVENT",
	            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
	        
	        if (result == JOptionPane.OK_OPTION) {
	        	String title = eventTitle.getText();
	        	String dateStr = date.getText();
	        	String startStr = startTime.getText();
	        	String endStr = endTime.getText();
	            model.createEvent(title, dateStr, startStr, endStr);
	        } else {
	            System.out.println("Cancelled");
	        }
			
		}
    	
    }
    
    class quit_Action implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try{
				model.quit();
			}
			catch(Exception expt){
				System.out.println("Load Failed!");
			}
		}
    }
}