import java.awt.*;
import java.io.IOException;

import javax.swing.JFrame;


public class SimpleCalendar {
	static JFrame frmMain;
	
	public static void main(String[] args) throws ClassNotFoundException, IOException{
		frmMain = new JFrame ("Calendar"); //Create frame
		frmMain.setSize(680, 375); //Set size to 400x400 pixels
		frmMain.setLayout(new BorderLayout());
		
		Model model = new Model();
		CalendarPanel myPanel = new CalendarPanel(model);
		DayViewPanel dayPanel = new DayViewPanel(model);
		model.attach(myPanel);
		model.attach(dayPanel);
		
		frmMain.add(myPanel, BorderLayout.CENTER);
		frmMain.add(dayPanel, BorderLayout.EAST);
		frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Close when X is clicked
		frmMain.setResizable(false);
		frmMain.setVisible(true);
	}
}
