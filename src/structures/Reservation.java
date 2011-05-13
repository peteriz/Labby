package structures;

import java.util.Calendar;

public class Reservation {
	private final int instrumentid;
	private final String username;
	private final int reservationid;
	private final Calendar start_time;
	private final Calendar end_time;
	
	public Reservation(int instrumentid, String username, int reservationid,
			long start_time, long end_time) {
		
		this.instrumentid = instrumentid;
		this.username = username;
		this.reservationid = reservationid;
		this.start_time = Calendar.getInstance(); 
		this.start_time.setTimeInMillis(start_time);
		this.end_time = Calendar.getInstance(); 
		this.end_time.setTimeInMillis(end_time);
	}

	public int getInstrumentid() {
		return instrumentid;
	}

	public String getUsername() {
		return username;
	}

	public int getReservationid() {
		return reservationid;
	}

	public Calendar getStart_time() {
		return start_time;
	}

	public Calendar getEnd_time() {
		return end_time;
	}
	
	
}
