package utilities;


/**
 * A class that handles all the properties that the proxy server holds.
 * The properties are read from the proxy.properties file.
 * @author s7355342
 *
 */
public class ServerProperties {
	
	/* Variables of ProxyProperties */
	private static String DBusername;
	private static String DBpassword;
	private static Integer DBport;
	private static String database;
	private static Integer maximumDBConnections;
	private static String masterPassword;
	private static String emailUserName;
	private static String emailPassword;
	private static ServerProperties instance = null;
	
	static {
		getInstance();
	}
	public synchronized static ServerProperties getInstance() {
		if (instance == null) {
			instance = new ServerProperties();
		}
		return instance;
	}
	
	
	public String getMasterPassword() {
		return masterPassword;
	}
	public void setDBusername(String dBusername) {
		DBusername = dBusername;
	}

	public void setDatabase(String database) {
		ServerProperties.database = database;
	}
	
	public void setDBpassword(String dBpassword) {
		DBpassword = dBpassword;
	}

	public void setDBport(Integer dBport) {
		DBport = dBport;
	}

	public void setMaximumDBConnections(Integer maximumDBConnections) {
		ServerProperties.maximumDBConnections = maximumDBConnections;
	}

	public void setMasterPassword(String masterPassword) {
		ServerProperties.masterPassword = masterPassword;
	}

	public void setEmailUserName(String emailUserName) {
		ServerProperties.emailUserName = emailUserName;
	}

	public void setEmailPassword(String emailPassword) {
		ServerProperties.emailPassword = emailPassword;
	}
	/**
	 * Initialize a new properties object, 
	 * feed all the parameters from proxy.properties file.
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public ServerProperties()  {
		
		
		
		/*
		DBusername = "speter";
		DBpassword = "speter";
		DBport = 3306;
		database = "speterDB";
		maximumDBConnections = 5;
		masterPassword = "master";
		emailUserName= "labby.lab.managment@gmail.com";
		emailPassword= "3y87f4igtv37t3wgivh4yn3";
		
		if (props == null){
			props = new Properties();
			props.load(new FileInputStream(createProperties()));
		}
		
		try{
			DBusername = props.getProperty("DB.username").trim();
			DBpassword = props.getProperty("DB.password").trim();
			DBport = Integer.parseInt(props.getProperty("DB.port").trim());
			database = props.getProperty("DB.database").trim();
			maximumDBConnections = Integer.parseInt(props.getProperty("DB.maxConnections").trim());
			masterPassword = props.getProperty("password");
			emailUserName=props.getProperty("emailUserName").trim();
			emailPassword=props.getProperty("emailPassword").trim();
		} catch (NumberFormatException ex){
			throw new NumberFormatException("Illegal number in properties file.");
		}*/
	}
	
	/**
	 * Returns the DB username
	 * @return Username
	 */
	public String getUsername() {
		return DBusername;
	}

	/**
	 * Returns the DB username password
	 * @return DB username password
	 */
	public String getPassword() {
		return DBpassword;
	}
	
	/**
	 * Returns the DB connection port
	 * @return port
	 */
	public Integer getDatabasePort() {
		return DBport;
	}

	/**
	 * Returns the DB name
	 * @return DB name
	 */
	public String getDatabase() {
		return database;
	}
	
	public Integer getMaximumDBConnections() {
		return maximumDBConnections;
	}
	
	public String getEmailUserName() {
		return emailUserName;
	}

	public String getEmailPassword() {
		return emailPassword;
	}

	/*private static File createProperties() throws IOException{
		
		File props = new File("labServer.properties");
		if (!props.exists()){
			BufferedWriter bw = new BufferedWriter(new FileWriter(props));
			bw.write("DB.username=speter");
			bw.newLine();
			bw.write("DB.password=speter");
			bw.newLine();
			bw.write("DB.port=3306");
			bw.newLine();
			bw.write("DB.database=speterDB");
			bw.newLine();
			bw.write("DB.maxConnections=5");
			bw.newLine();
			bw.write("password=password");
			bw.newLine();
			bw.write("emailUserName=labby.lab.managment@gmail.com");
			bw.newLine();
			bw.write("emailPassword=3y87f4igtv37t3wgivh4yn3");
			bw.newLine();
			bw.flush();
			bw.close();
		}
		return props;
	}*/
	
}
