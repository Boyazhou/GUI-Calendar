import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Model {
	private TreeMap<GregorianCalendar, ArrayList<Event>> eventCollection;
	private GregorianCalendar printCalendar;
	private GregorianCalendar realTime;
	private ArrayList<ChangeListener> listeners;
	private DateFormat df;
	public static final Scanner in = new Scanner(System.in);
	
	public Model() throws ClassNotFoundException, IOException{
		eventCollection = new TreeMap<GregorianCalendar, ArrayList<Event>>();
		listeners = new ArrayList<ChangeListener>();
		printCalendar = new GregorianCalendar();
		realTime = new GregorianCalendar();
		df = new SimpleDateFormat("MM/dd/yyyy");
		this.load();
	}
	
	/**
	 * attach ChangeListener
	 * @param c
	 */
	public void attach(ChangeListener c)
	{
		listeners.add(c);
	}
	
	
	
	public GregorianCalendar getPrintCal() {return printCalendar; }
	
	public GregorianCalendar getRealTime() {return realTime; }
	
	/**
	 * Get the event of current view day
	 * @return the event on current day (Not today, but the day you are viewing) 
	 */
	public ArrayList<Event> getEvent() {
		GregorianCalendar checkEvent = new GregorianCalendar(printCalendar.get(Calendar.YEAR), printCalendar.get(Calendar.MONTH), printCalendar.get(Calendar.DAY_OF_MONTH)); 
		if(eventCollection.containsKey(checkEvent)){
			ArrayList<Event> curEvent = eventCollection.get(checkEvent);
			System.out.println(curEvent);
			return curEvent;
		}
		else
			return null;
	}
	
	/**
	 * Change the printCalendar to the next day
	 */
	public void nextDay() { 
		printCalendar.add(Calendar.DATE, 1);
		for (ChangeListener l : listeners)
	    {
	       l.stateChanged(new ChangeEvent(this));    
	    }
	}
	
	/**
	 * Change the printCalendar to the previous day
	 */
	public void preDay() { 
		printCalendar.add(Calendar.DATE, -1);
		for (ChangeListener l : listeners)
	    {
	         l.stateChanged(new ChangeEvent(this));    
	    }
	}
	/**
	 * set the date of the view within the current month
	 */
	public void setDate(int date){
		printCalendar.set(printCalendar.get(Calendar.YEAR), printCalendar.get(Calendar.MONTH), date);
		for (ChangeListener l : listeners)
	    {
	         l.stateChanged(new ChangeEvent(this));    
	    }
	}
	
	/**
	 * Load all the events from file
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void load() throws IOException, ClassNotFoundException{
		File newFile = new File("events.ser");
		if(!newFile.exists()) {
			newFile.createNewFile();
			System.out.println("This is the first run!");
			return;
		}	     
		FileInputStream fileIn = new FileInputStream("events.ser");
		try{
			ObjectInputStream in = new ObjectInputStream(fileIn);  
			Object obj = null;
	        while((obj = in.readObject()) != null) {
	        	Event tempEvent = (Event)obj;
	        	GregorianCalendar tempEventCal = new GregorianCalendar(tempEvent.getStartTime().get(Calendar.YEAR),
	        			tempEvent.getStartTime().get(Calendar.MONTH), tempEvent.getStartTime().get(Calendar.DAY_OF_MONTH));
	        	if(eventCollection.containsKey(tempEventCal)){
		        	eventCollection.get(tempEventCal).add(tempEvent);
		        }
		        else{
		        	ArrayList newEvents = new ArrayList();
		        	newEvents.add(tempEvent);
		        	eventCollection.put(tempEventCal, newEvents);
		        }
	        }
		}
		catch(EOFException ex){
			System.out.println("Load Success!");
		}
		
		//Notify changelisteners
		for (ChangeListener l : listeners)
		{
		    l.stateChanged(new ChangeEvent(this));    
		}
	}
	
	/**
	 * leave the calendar and save all the events to file
	 * @throws IOException
	 */
	public void quit() throws IOException{
		File newFile = new File("events.ser");
		if(!newFile.exists()) {
			newFile.createNewFile();
		}	     
		FileOutputStream fileOut = new FileOutputStream("events.ser");
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		List<Map.Entry<GregorianCalendar, ArrayList<Event>>> list = 
				new LinkedList<Map.Entry<GregorianCalendar, ArrayList<Event>>>(eventCollection.entrySet());
		for(Map.Entry<GregorianCalendar, ArrayList<Event>> entry : list){
			/*GregorianCalendar tempCal = entry.getKey();
			out.writeObject(tempCal);*/
			ArrayList<Event> tempEvents = entry.getValue();
			for(Event e: tempEvents)
				out.writeObject(e);
		}	         
		out.close();
        fileOut.close();
        System.out.println("You have left the calendar!");
	}
	
	
	
	
	/**
	 * Create an event
	 */
	public void createEvent(String title, String dateStr, String startTime, String endTime ){
		try{			
			//create eventCal, which is the key
	        Date date = df.parse(dateStr);
	        GregorianCalendar eventCal = new GregorianCalendar();
	        eventCal.setTime(date);
	        //create newEvent, which is the value
	        //create start time
	        DateFormat dfHour = new SimpleDateFormat("MM/dd/yyyy HH:mm");
	        Date start = dfHour.parse(dateStr + " " + startTime);
	        //enter and create ending time
	        endTime = endTime.toUpperCase();
	        Date end = null;
	        if(!endTime.equals("NA")){
	         end = dfHour.parse(dateStr + " " + endTime);
	        }
	        Event newEvent = new Event(title, start, end);
	        //put the key-value into eventCollection
	        if(eventCollection.containsKey(eventCal)){
	        	boolean conflict = false;
	        	//Check if two events conflicts
	        	for(Event e: eventCollection.get(eventCal)){
	        		if(newEvent.checkConflict(e)){
	        			conflict = true;
	        			String info = "The event is conflicted with other events";
	        			JOptionPane.showMessageDialog(null, info, "CONFLICT", JOptionPane.INFORMATION_MESSAGE);
	        		}
	        	}
	        	if(!conflict)
	        		eventCollection.get(eventCal).add(newEvent);
	        }
	        else{
	        	ArrayList newEvents = new ArrayList();
	        	newEvents.add(newEvent);
	        	eventCollection.put(eventCal, newEvents);
	        }
	        
	        for (ChangeListener l : listeners)
		    {
		         l.stateChanged(new ChangeEvent(this));    
		    }
		}
		catch(ParseException exp){
			String info = "Please put the correct format for the date and time!";
			JOptionPane.showMessageDialog(null, info, "ERROR", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
	}
}
