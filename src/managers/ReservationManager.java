package managers;

import java.sql.SQLException;
import java.util.LinkedList;


import structures.FreeSlot;
import structures.Reservation;
import utilities.DatabaseUtils;
import structures.ReturnValues;

public class ReservationManager {

	public static LinkedList<FreeSlot> findFreeSlots(int instrumentid,
			long date, long minutes, int numOfTimeSlots) throws SQLException{
		
		Integer slotLength = InstrumentManager.getSlotLength(instrumentid);
		if (slotLength == null){
			return null;
		}
		
		return DatabaseUtils.getInstance().findFreeSlots(instrumentid,
								date,minutes,numOfTimeSlots,slotLength);
	}
	
	public static LinkedList<Reservation> findReservations() throws SQLException{
		
		return DatabaseUtils.getInstance().findReservations(null);
	}
	
	public static LinkedList<Reservation> findReservations(String username) throws SQLException{
		return DatabaseUtils.getInstance().findReservations(username);
	}
	
	public static ReturnValues deletReservation(int reservationID) throws SQLException{
		return DatabaseUtils.getInstance().deletReservation(reservationID, null);
	}
	
	public static ReturnValues deletReservation(int reservationID, String username) throws SQLException{
		return DatabaseUtils.getInstance().deletReservation(reservationID, username);
	}

	public static ReturnValues makeReservation(String userName,
			Integer usernumber, Integer instId, long timeInMillis, int i,
			Integer numOfTimeSlots) throws SQLException {
		
		return DatabaseUtils.getInstance().makeReservation(
				userName, usernumber,instId, timeInMillis, i, numOfTimeSlots);
	}
	
	public static ReturnValues archiveReservations() throws SQLException{
		return DatabaseUtils.getInstance().archiveReservations();
	}

	public static String[][] getTotalReservations() throws SQLException {
		return DatabaseUtils.getInstance().getTotalReservations();
	}
}
