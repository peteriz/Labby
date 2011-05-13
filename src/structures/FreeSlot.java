package structures;

import java.util.Calendar;

public class FreeSlot {
	private final Calendar cal;
	private final int options;
	
	public FreeSlot(long date, int options){
		this.options = options;
		cal = Calendar.getInstance();
		cal.setTimeInMillis(date);
	}
	
	public Calendar getCal(){
		return cal;
	}
	
	public int getOptions(){
		return options;
	}
}
