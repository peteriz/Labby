package structures;

import java.util.LinkedList;

public class UserDetails implements Comparable<UserDetails>{

	private final String username;
	private final Integer usernumber;
	private String fullname;
	private String groupname;
	private String phonenum;
	private String address;
	private boolean active;
	private boolean isManager;
	private LinkedList<Integer> permissions;
	
	public UserDetails(String username,Integer usernumber, String fullname, String groupname,
			String phonenum, String address, boolean active, boolean isManager,
			LinkedList<Integer> permissions) {
		
		this.username = username;
		this.usernumber = usernumber;
		this.fullname = fullname;
		this.groupname = groupname;
		this.phonenum = phonenum;
		this.address = address;
		this.active = active;
		this.isManager = isManager;
		this.permissions = permissions;
	}

	public String getUsername() {
		return username;
	}
	
	public Integer getUsernumber() {
		return usernumber;
	}

	public String getFullname() {
		return fullname;
	}

	public String getGroupname() {
		return groupname;
	}

	public String getPhonenum() {
		return phonenum;
	}

	public String getAddress() {
		return address;
	}

	public Boolean isActive() {
		return active;
	}

	public LinkedList<Integer> getPermissions() {
		return permissions;
	}

	public boolean isManager() {
		return isManager;
	}

	public String toXML() {
		
		String retVal = "<RESPONSE><FULLNAME>"+fullname+"</FULLNAME><ACTIVE>"+
						(active?"1":"0")+"</ACTIVE><GROUPNAME>"+groupname+
						"</GROUPNAME><PHONENUM>"+phonenum+"</PHONENUM><ADDRESS>"+
						address+"</ADDRESS>";
		for (Integer permission : permissions){
			retVal+="<PERMISSION>" + permission + "</PERMISSION>";
		}
		retVal +="</RESPONSE>";
		return retVal;
	}

	@Override
	public int compareTo(UserDetails o) {
		return username.compareTo(o.username);
	}

	public void setManager() {
		isManager=true;
	}
}
