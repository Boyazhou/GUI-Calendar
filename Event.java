import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Event class: hold a single event
 * @author Administrator
 *
 */
public class Event implements Serializable {
	private String name;
	private GregorianCalendar startTime;
	private GregorianCalendar endingTime;
	private DateFormat df;
	
	public Event(String name, Date start, Date end) {
		df = new SimpleDateFormat("HH:mm");
		this.name = name;
		startTime = new GregorianCalendar();
		startTime.setTime(start);
		if(end != null){
			endingTime = new GregorianCalendar();
			endingTime.setTime(end);
		}
	}
	/**
	 * accessor
	 * @return
	 */
	public String getName() {return name;}
	public GregorianCalendar getStartTime(){
		return startTime;
	}
	public GregorianCalendar getEndingTime(){
		return endingTime;
	}
	
	public String toString(){
		String res = getName() + " " + df.format(startTime.getTime());
		if(endingTime != null)
			res = res + " - " + df.format(endingTime.getTime());
		return res;
	}
	/**
	 * Mutator
	 * @param n
	 */
	public void setName(String n) { name = n;}
	public void setStartTime(GregorianCalendar start) {startTime = start;}
	public void setEndingTime(GregorianCalendar end) {endingTime = end;}
	/**
	 * Check if two events conflicts 
	 * pre:the two events are on the same day
	 * @param e
	 * @return
	 */
	public boolean checkConflict(Event e){
		//this event is earlier than e	
		if(this.getEndingTime() == null){
			if(this.getStartTime().before(e.getStartTime()))
				return false;
		}		
		else if(this.getEndingTime().before(e.getStartTime()))
			return false;
		
		//this event is later than e
		if(e.getEndingTime() == null){
			if(e.getStartTime().before(this.getStartTime()))
				return false;
		}
		else if(e.getEndingTime().before(this.getStartTime()))
			return false;
		return true;
	}
}
