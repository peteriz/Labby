package managers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import structures.Instrument;
import utilities.DatabaseUtils;

public class InstrumentManager {

	private static final int LIMIT = 20;
	private static final HashMap<Integer, Integer> memory = new HashMap<Integer, Integer>(LIMIT);
	private static final Queue<Integer> age = new LinkedList<Integer>();
	
	public static LinkedList<Instrument> getPermitedInstruments(Integer usernumber) throws SQLException{
		
		return  DatabaseUtils.getInstance().getPermitedInstruments(usernumber);
	}
	
	public static LinkedList<Instrument> getAllInstruments() throws SQLException{
		
		return  DatabaseUtils.getInstance().getAllInstruments();
	}
	public static Integer getSlotLength(Integer instrumentid) throws SQLException{
		Integer retVal = memory.get(instrumentid);
		if (retVal != null){
			return retVal;
		}
		else {
			retVal = retrieveSlotLength(instrumentid);
			return retVal;
		}
	}

	private synchronized static Integer retrieveSlotLength(Integer instrumentid) throws SQLException {
		
		if (memory.containsKey(instrumentid)){
			return memory.get(instrumentid);
		}
		Integer retVal = DatabaseUtils.getInstance().retrieveSlotLength(instrumentid);
		if (retVal == null){
			return null;
		}
		else {
			memory.put(instrumentid, retVal);
			age.add(instrumentid);
			if (age.size() > LIMIT){
				memory.remove(age.poll());
			}
			return retVal;
		}
	}
	
	public static Integer addInstrument(String instType, int instTimeslot, 
					String instDesc, int instPermission) throws SQLException{
		
		Integer retVal = DatabaseUtils.getInstance().addInstrument(
						instType, instTimeslot, instDesc, instPermission);
		if (retVal == null){
			return null;
		}
		else {
			setSlotLength(retVal, instTimeslot);
			return retVal;
		}
	}

	private synchronized static void setSlotLength(Integer instrumentid, int instTimeslot) {
		
		memory.put(instrumentid, instTimeslot);
		age.add(instrumentid);
		if (age.size() > LIMIT){
			memory.remove(age.poll());
		}
	}

	public static LinkedList<Instrument> searchInstruments(Integer usernumber,
			String username, String searchQuery) throws SQLException {
		
		return DatabaseUtils.getInstance().searchInstruments(usernumber, username, searchQuery);
	}
	
}
