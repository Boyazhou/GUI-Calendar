import java.awt.*;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.*;


public class DayViewPanel extends JPanel implements ChangeListener{
	private Model model;
	private JTable table;
	private JScrollPane pane;
	static String headers[] = { "12am", "1am", "2am", "3am", "4am", "5am", "6am", "7am", "8am", "9am", "10am", "11am",
				"12pm", "1pm",  "2pm", "3pm", "4pm", "5pm", "6pm", "7pm", "8pm", "9pm", "10pm", "11pm"};
	private static String[] dayOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
	
	public DayViewPanel(Model m){
		//Look and feel
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
		catch (ClassNotFoundException e) {}
		catch (InstantiationException e) {}
		catch (IllegalAccessException e) {}
		catch (UnsupportedLookAndFeelException e) {}
		
		this.model = m;
		setLayout(new BorderLayout());
		
		DefaultTableModel tableModel = new DefaultTableModel(){public boolean isCellEditable(int rowIndex, int mColIndex){return false;}};
	    table = new JTable(tableModel);
	     pane = new JScrollPane(table);
		
		//Set row/column count
		table.setRowHeight(75);
		tableModel.setColumnCount(2);
		tableModel.setRowCount(24);
		for(int i = 0; i < 24; i++)
			table.setValueAt(headers[i], i, 0);
		
		table.setShowGrid(false);
		table.getColumnModel().getColumn(0).setHeaderValue("");
		table.getColumnModel().getColumn(0).setPreferredWidth(30);
		table.getColumnModel().getColumn(1).setPreferredWidth(300);
		
		//No resize/reorder
		table.getTableHeader().setResizingAllowed(false);
		table.getTableHeader().setReorderingAllowed(false);
		
		table.setPreferredScrollableViewportSize(new Dimension(300, 200));
	
		add(pane);
		
		table.setDefaultRenderer(table.getColumnClass(0), new tableRenderer());
		//align header
		((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
		refreshCalendar();
	}
	
	
	
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
	}
	
	/**
	 * refresh the view of the day view calendar
	 */
	public void refreshCalendar(){
		//clear table
		for(int i = 0; i < 24; i++){
			table.setValueAt(null, i, 1);
		}
		//Repaint table header for the date
		JTableHeader th = table.getTableHeader();
		TableColumnModel tcm = th.getColumnModel();
		TableColumn tc = tcm.getColumn(1);
		//get time
		String curDayOfWeek = dayOfWeek[model.getPrintCal().get(Calendar.DAY_OF_WEEK) - 1];	
		int curMonth = model.getPrintCal().get(Calendar.MONTH);
		int curDate = model.getPrintCal().get(Calendar.DAY_OF_MONTH);	
		int curYear = model.getPrintCal().get(Calendar.YEAR);
		String headerStr = curDayOfWeek + " " + (curMonth + 1) + "/" + curDate;	
		tc.setHeaderValue(headerStr);
		th.repaint();
	    
		//set initial position of scroll bar
		int rowIndex = model.getPrintCal().get(Calendar.HOUR_OF_DAY);
		table.getSelectionModel().setSelectionInterval(rowIndex, 1);
		table.scrollRectToVisible(new Rectangle(table.getCellRect(rowIndex, 1, true)));
		//set events to the day view
		ArrayList<Event> ents = model.getEvent();
		if(ents != null){
			for(Event e: ents){
				int startHour = e.getStartTime().get(Calendar.HOUR_OF_DAY);
				table.setValueAt(e, startHour, 1);
			}
		}
		table.repaint();
	}
	
	class tableRenderer extends DefaultTableCellRenderer{
		public Component getTableCellRendererComponent (JTable table, Object value, boolean selected, boolean focused, int row, int column){
			super.getTableCellRendererComponent(table, value, selected, focused, row, column);
			setHorizontalAlignment( JLabel.CENTER );
			if(column == 0)
				setHorizontalAlignment( JLabel.RIGHT );
			setBackground(new Color(255, 255, 255));
			
			if(column == 1){ //  t  l  b  r
	            setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
	            setBackground(new Color(225, 225, 225));
			}
			setForeground(Color.black);
			return this;  
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		refreshCalendar();
	}
}
