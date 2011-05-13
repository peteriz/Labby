package structures;

public class Instrument {
	
	private final String type,description;
	private final Integer instrumentid,timeslotlength, permission;
	
	public Instrument(String type, String description, Integer instrumentid,
			Integer timeslotlength, Integer permission) {
		this.type = type;
		this.description = description;
		this.instrumentid = instrumentid;
		this.timeslotlength = timeslotlength;
		this.permission = permission;
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public Integer getInstrumentid() {
		return instrumentid;
	}

	public Integer getTimeslotlength() {
		return timeslotlength;
	}
	
	public Integer getPermission() {
		return permission;
	}
	
}
