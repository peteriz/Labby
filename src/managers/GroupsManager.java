package managers;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import utilities.DatabaseUtils;
import structures.ReturnValues;

public class GroupsManager {

	private static Set<String> groups = Collections.synchronizedSet(new HashSet<String>());
	private static boolean all = false;
	
	public static synchronized Set<String> getGroups() throws SQLException{
		
		if (all){
			return groups;
		}
		else{
			Set<String> grps = DatabaseUtils.getInstance().getGroups();
			if (grps == null){
				return null;
			}
			try{
				groups.addAll(grps);
			} catch (UnsupportedOperationException e){
				for(String group:grps){
					groups.add(group);
				}
			}
			all=true;
			return grps;
		}
		
	}
	
	public static ReturnValues addGroup(String groupName) throws SQLException{
		
		ReturnValues retVal = DatabaseUtils.getInstance().addGroup(groupName);
		if (retVal == null){
			return ReturnValues.FAILURE;
		}
		if (retVal.equals(ReturnValues.SUCCESS)){
			groups.add(groupName);
		}
		return retVal;
	}
	
	public static Boolean isGroup(String groupname) throws SQLException{
		
		if (groups.contains(groupname)){
			return true;
		}
		
		Boolean retVal = DatabaseUtils.getInstance().isGroup(groupname);
		if (retVal == null){
			return null;
		}
		else if(retVal){
			groups.add(groupname);
		}
		return true;
	}
}
