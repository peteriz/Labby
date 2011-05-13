package utilities;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import structures.FreeSlot;
import structures.Instrument;
import structures.Reservation;
import structures.ReturnValues;
import structures.UserDetails;

/**
 * 
 * @author Noam Segev, Peter Izsak
 * SQLconnector
 * A class for handling a connection to a mysql Database, basic queries,
 * inserts and deletes.
 * Also, it has methods for dealing with cache and log tables.
 */
public class DatabaseUtilsNew {

	private static final String DUPLICATE_ENTRY = "Duplicate entry";
	public static final String DATABASE_UNAVAILABLE = "Database unavailable";
	public static final String NO_FREE_SOCKETS = "Can't find free sockets to DB";
	private static final String SERVER_RESTARTED = "there was a server restart";
	private static final int RETRY_TIME = 1000;
	
	/* connection parameters */
	private String SQLserver;
	public void setSQLserver(String sQLserver) {
		SQLserver = sQLserver;
	}



	public void setSQLuserName(String sQLuserName) {
		SQLuserName = sQLuserName;
	}



	public void setSQLpassword(String sQLpassword) {
		SQLpassword = sQLpassword;
	}



	public void setSQLport(int sQLport) {
		SQLport = sQLport;
	}



	public void setSQLmaxcon(int sQLmaxcon) {
		SQLmaxcon = sQLmaxcon;
	}
	
	public void setSQLdbname(String sQLdbname) {
		SQLdbname = sQLdbname;
	}



	private static String SQLuserName;
	private static String SQLpassword;
	private static String SQLdbname;
	private static int SQLport;
	private static int SQLmaxcon;


	private static DatabaseUtilsNew instance = null;
	
	private Connection[] cons;
	private boolean[] available;
	private HashMap<Integer,ResultSet> mapping = new HashMap<Integer, ResultSet>();

	// Static constructor, loads jdbc to memory
	static {
		
		try {
			Class.forName("com.mysql.jdbc.Driver"); 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println("jdbc connecter not in the system. Exiting");
			System.exit(1);
		}		
		getInstance();
		
	}
	
	public synchronized static DatabaseUtilsNew getInstance(){
		/*if (instance == null){
			try {
				instance = new DatabaseUtilsNew();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//TODO remove this else
		else {*/
			try {
				instance.createStoredProcedures();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		//}
		
		return instance;
	}
	
	
	
	/**
	 * Constructor that gets a property object and initializes the DB connection 
	 * parameters
	 * @param properties a properties file
	 * @throws SQLException 
	 */
	protected DatabaseUtilsNew() throws SQLException{

		SQLserver = "jdbc:mysql://localhost:" + SQLport + "/" + SQLdbname
					+ "?allowMultiQueries=true";

		cons = new Connection[SQLmaxcon];
		available = new boolean[SQLmaxcon];
		for (int i=0;  i<cons.length; ++i){
			try {
				cons[i]=DriverManager.getConnection (SQLserver, SQLuserName, SQLpassword);
				available[i] = true;
			} catch (SQLException e) {
				try {
					closeConnections();
				} catch (SQLException e2) {}
				throw new SQLException(DATABASE_UNAVAILABLE);
			}
		}
		
		try {
			dbStartup();
		} catch (SQLException e) {
			try {
				closeConnections();
			} catch (SQLException e2) {
			}
			throw new SQLException(DATABASE_UNAVAILABLE);
		}
	}

	private synchronized void closeConnections() throws SQLException{
		
		for  (int i=0; i<cons.length; ++i){
			if (cons[i] !=  null){
				cons[i].close();
				cons[i] = null;
				available[i] = false;
			}
		}
	}

	private synchronized int findAndCatch() throws SQLException{
		
		int nulls = 0;
		
		for (int i=0; i<cons.length; ++i){
			if (cons[i] == null){
				++nulls;
				available[i] = false;
			}
			else if (available[i]){
				available[i] = false;
				return i;
			}
		}
		
		if (nulls == cons.length){
			if (instance != null){
				if (instance.cons[0] == null){
					instance = null;
					mapping.clear();
					if (getInstance() == null){
						throw new SQLException(DATABASE_UNAVAILABLE);
					}
				}
				throw new SQLException(SERVER_RESTARTED);
			}
			else if (getInstance() == null){
				throw new SQLException(DATABASE_UNAVAILABLE);
			}
		}
		return -1;
	}

	/**
	 * Executes an SQL query
	 * 
	 * @param query a SQL query as String
	 * @return ResultSet of query
	 * @throws SQLException 
	 */
	private Integer query(String query) throws SQLException{

		//TODO remove this printline
		System.out.println(query);
		
		int conNum = findAndCatch();
		
		// If no server connection is available, wait a little and try again
		if (conNum == -1) {
			try {
				Thread.sleep(RETRY_TIME+Math.round(500*Math.random()));
			} catch (InterruptedException e) {
			}
			
			conNum = findAndCatch();
			if (conNum == -1){
				try {
					Thread.sleep(RETRY_TIME+Math.round(1000*Math.random()));
				} catch (InterruptedException e) {
				}
				
				conNum = findAndCatch();
				if (conNum == -1){
					throw new SQLException(NO_FREE_SOCKETS);
				}
			}
		}
		
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = cons[conNum].createStatement();
			rs = stmt.executeQuery(query);
			mapping.put(conNum, rs);
			return conNum;
		} catch (SQLException e) {
			mapping.remove(conNum);
			try {
				stmt.close();
			} catch (Exception e1){}
			if (e.getMessage().contains("Table") && e.getMessage().contains("doesn't exist")){
				available[conNum]=true;
				return -1;
			}
			else if (e.getMessage().contains(DUPLICATE_ENTRY)){
				available[conNum]=true;
				throw e;
			}
			else if (!e.getMessage().contains(SERVER_RESTARTED) && !e.getMessage().contains(DATABASE_UNAVAILABLE) &&
					!e.getMessage().contains(NO_FREE_SOCKETS)){
				cons[conNum]=null;
				return query(query);
			}
			else {
				cons[conNum]=null;
				throw e;
			}
		}	
	}

	/**
	 * Execute a SQL DDL
	 * @param cmd a SQL DDL as string
	 * @throws SQLException
	 */
	private void execute(String cmd) throws SQLException{
		
		//TODO remove this printline
		System.out.println(cmd);
		
		int conNum = findAndCatch();
		// If no server connection is available, wait a little and try again
		if (conNum == -1) {
			try {
				Thread.sleep(RETRY_TIME+Math.round(500*Math.random()));
			} catch (InterruptedException e) {
			}
			
			conNum = findAndCatch();
			if (conNum == -1){
				try {
					Thread.sleep(RETRY_TIME+Math.round(1000*Math.random()));
				} catch (InterruptedException e) {
				}
				
				conNum = findAndCatch();
				if (conNum == -1){
					throw new SQLException(NO_FREE_SOCKETS);
				}
			}
		}
		
		Statement stmt = null;
		try{
			stmt = cons[conNum].createStatement();
			stmt.execute(cmd);
		} catch (SQLException e) {
			if (e.getMessage().startsWith(DUPLICATE_ENTRY)){
				throw new SQLException(DUPLICATE_ENTRY);
			}
			else if (!e.getMessage().contains(SERVER_RESTARTED) && !e.getMessage().contains(DATABASE_UNAVAILABLE) &&
					!e.getMessage().contains(NO_FREE_SOCKETS)){
				cons[conNum]=null;
				execute(cmd);
			}
			else {
				cons[conNum] = null;
				e.printStackTrace();
			}
		} finally  {
			try {
				stmt.close();
			} catch (Exception e) {}
			available[conNum] = true;
		}

	}

	private void dbStartup() throws SQLException{
		
		Integer result = query("select 1 from user_dbusers ");
		if (result == -1){
			createApplicationTables();
			createStoredProcedures();
			loadInitialDate();
		}
		else {
			mapping.get(result).close();
			mapping.remove(result);
			available[result] = true;
		}
	}
	
	private void loadInitialDate() {
		
		// TODO Auto-generated method stub
		// Create users
		// Create groups
		// Createinstruments
		// Create reservations
		
	}

	private void createStoredProcedures() throws SQLException {
		
		// Create stored procedure to add a username
		execute("DROP PROCEDURE IF EXISTS `addUser`");
		execute("CREATE PROCEDURE `addUser`(IN name VARCHAR(10), IN password VARCHAR(16), " +
				"IN phonenum VARCHAR(16), IN address VARCHAR(50), IN fullname VARCHAR(50)) " +
				"BEGIN "+
				"DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK; " +
				"START TRANSACTION; " +
				"SELECT max(usernumber) FROM user_dbusers INTO @tmp FOR UPDATE; " +
				"IF @tmp IS NULL OR @tmp<0 THEN SET @tmp=0; END IF; " +
				"INSERT INTO user_dbusers (usernumber,username,password) values (@tmp+1, name,password); " +
				"INSERT INTO user_personalinfo (username,phonenum,address) values (name,phonenum,address); " +
				"INSERT INTO user_info (username,fullname) values (name,fullname); " +
				"SELECT count(*) FROM user_dbusers WHERE usernumber=@tmp INTO @tmp2; " +
				"IF @tmp2=1 THEN COMMIT; SELECT @tmp+1;" +
				"ELSE ROLLBACK; SELECT -1; END IF; " +
				"END");
		
		// Create stored procedure to change user info
		execute("DROP PROCEDURE IF EXISTS `changeInfo`");
		execute("CREATE PROCEDURE `changeInfo`(IN cmnd TEXT, IN number INT) " +
				"BEGIN " +
				"DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK; " +
				"START TRANSACTION; " +
				"SET @s=cmnd; " +
				"PREPARE torun FROM @s; EXECUTE torun; DEALLOCATE PREPARE torun; " +
				"SELECT count(*) FROM user_dbusers WHERE usernumber=number INTO @tmp; " +
				"IF @tmp=1 THEN COMMIT; " +
				"ELSE ROLLBACK; END IF; " +
				"COMMIT; END");
		
		// Create stored procedure to add permissions to a user
		execute("DROP PROCEDURE IF EXISTS `addPermission`");
		execute("CREATE PROCEDURE `addPermission`(IN name VARCHAR(10), IN number INT, IN per INT) " +
				"BEGIN START TRANSACTION; "+
				"INSERT INTO user_permissions values(name,per); " +
				"SELECT count(*) FROM user_permissions a, user_dbusers b WHERE b.usernumber=number AND a.username=b.username AND a.username=name AND a.permission=per INTO @tmp; " +
				"IF @tmp=1 THEN COMMIT; ELSE ROLLBACK; END IF;" +
				"END");
		
		// Create stored procedure to add permissions to a user
		execute("DROP procedure IF EXISTS `removePermission`");
		execute("CREATE PROCEDURE `removePermission`(IN name VARCHAR(10), IN number INT, IN per INT) " +
				"BEGIN START TRANSACTION; "+
				"DELETE FROM user_permissions WHERE username=name AND permission=per; " +
				"SELECT count(*) FROM user_dbusers WHERE usernumber=number INTO @tmp; " +
				"IF @tmp!=1 THEN ROLLBACK; ELSE COMMIT; END IF;" +
				"END");
		
		// Create stored procedure to add instrument
		execute("DROP procedure IF EXISTS `addInstrument`");
		execute("CREATE PROCEDURE `addInstrument`(IN insttype VARCHAR(20),  IN timeslotlen INT, "+
				"IN indescription VARCHAR(512), IN permission SMALLINT) " +
				"BEGIN DECLARE total INT; "+
				"SELECT 1440/timeslotlen INTO total; " +
				"INSERT INTO instrument_info VALUES(NULL, insttype, timeslotlen, total, indescription, permission); " +
				"SELECT max(instrumentid) FROM instrument_info WHERE type=insttype AND description=indescription INTO @instid; " +
				"INSERT INTO reservations_archive SELECT @instid,0,groupname FROM groups; " +
				"SELECT @instid; " +
				"END");
		
		// Create stored procedure to add a new group
		execute("DROP procedure IF EXISTS `addGroup`");
		execute("CREATE PROCEDURE `addGroup`(IN newgroup VARCHAR(50)) " +
				"BEGIN " +
				"INSERT INTO groups VALUES(newgroup); " +
				"INSERT INTO reservations_archive SELECT instrumentid,0,newgroup FROM instrument_info; " +
				"END");
	
		// Create stored procedure to find free slots for an instrument
		execute("DROP procedure IF EXISTS `findFreeSlots`");
		execute("CREATE PROCEDURE `findFreeSlots`(IN instrument INT ,IN startDate BIGINT, IN startTime INT, IN k INT) " +
				"BEGIN " +
				"DECLARE total INT; " +
				"DECLARE slotLength INT; " +
				"START TRANSACTION;" +
				"SET @num = 0; " +
				"SELECT timeslotlength FROM instrument_info WHERE instrumentid=instrument INTO slotLength; " +
				"SELECT floor(1440/slotLength) INTO total; " +
				"SET @resrvationtablename=CONCAT('tb_',instrument,startDate,startTime,k,  SUBSTRING(CONVERT(RAND(), CHAR(5)),3 ));" +
				"SET @stmt = CONCAT('CREATE TABLE `',@resrvationtablename,'` AS (SELECT date, timeframe_start, timeframe_end, @num := (@num+1) AS indxNumber FROM reservations WHERE instrumentid=' ,instrument ,' AND (date+timeframe_end*',slotLength,'*" + PageUtils.MS_IN_MIN + ">=',startDate,'+ floor(',startTime,'/',slotLength,')*',slotLength,'*" + PageUtils.MS_IN_MIN + ") ORDER BY date ASC, timeframe_start ASC) ENGINE=MEMORY'); " +
				"PREPARE torun FROM @stmt; EXECUTE torun; DEALLOCATE PREPARE torun; " +
				"SET @stmt = CONCAT('SELECT count(*) FROM (SELECT *  FROM ', @resrvationtablename, ' WHERE date+timeframe_start*',slotLength,'*" + PageUtils.MS_IN_MIN + "<=',startDate,'+(1+floor(',startTime,'/',slotLength,'))*',slotLength,'*" + PageUtils.MS_IN_MIN + ") name INTO @tmp'); " +
				"PREPARE torun FROM @stmt; EXECUTE torun; DEALLOCATE PREPARE torun; " +
				"IF @tmp=0 THEN " +
				"SET @stmt = CONCAT('INSERT INTO ', @resrvationtablename, ' values(',startDate,',floor(',startTime,'/',slotLength,'),1+floor(',startTime,'/',slotLength,'),0)'); " +
				"PREPARE torun FROM @stmt; EXECUTE torun; DEALLOCATE PREPARE torun; END IF; " +
				"SET @stmt = CONCAT('SELECT max(indxNumber) FROM ', @resrvationtablename,'  INTO @maximum'); " +
				"PREPARE torun FROM @stmt; EXECUTE torun; DEALLOCATE PREPARE torun; " +
				"SET @stmt = CONCAT('SELECT ad,ate,floor(',total,'*(bd-ad)/" + PageUtils.MS_IN_DAY + "+bts-ate-',k,'+1) AS options FROM (SELECT A.date AS ad, A.timeframe_start AS ats, A.timeframe_end AS ate, B.date AS bd, B.timeframe_start AS bts, B.timeframe_end AS bte FROM ', @resrvationtablename,' A , ', @resrvationtablename,' B WHERE A.indxNumber=B.indxNumber-1 ORDER BY A.indxNumber ASC) otherName WHERE ',total,'*(bd-ad)/" + PageUtils.MS_IN_DAY + "+bts-ate >=',k,' LIMIT 4 UNION SELECT date AS ad, timeframe_end AS ate, 0 AS options FROM ', @resrvationtablename,'  WHERE indxNumber=@maximum'); " +
				"PREPARE torun FROM @stmt; EXECUTE torun; DEALLOCATE PREPARE torun; " +
				"SET @stmt = CONCAT('DROP TABLE ', @resrvationtablename); " +
				"PREPARE torun FROM @stmt; EXECUTE torun; DEALLOCATE PREPARE torun; " +
				"ROLLBACK; " +
				"END");
		
		// Create procedure to save reservations to the database
		execute("DROP procedure IF EXISTS `makeReservation`");
		execute("CREATE PROCEDURE `makeReservation`(IN instrument INT,IN usernumber INT, IN  username  VARCHAR(10), IN dateIn BIGINT, IN start_min INT, IN k INT)" +
				"BEGIN " +
				"DECLARE total INT; " +
				"DECLARE timeslot INT; " +
				"DECLARE slotLength INT; " +
				"SELECT timeslotlength FROM instrument_info WHERE instrumentid=instrument INTO slotLength; " +
				"SELECT floor(start_min/slotLength) INTO timeslot; " +
				"SELECT floor(1440/slotLength) INTO total; " +
				"START TRANSACTION; " +
				"INSERT INTO reservations VALUES(instrument,username,NULL,dateIn,timeslot,timeslot+k); " +
				"SELECT count(*) FROM (SELECT a.reservationid AS aresv, b.reservationid AS bresv FROM reservations a, reservations b  WHERE ((a.instrumentid = instrument) AND (a.instrumentid = b.instrumentid) AND (a.reservationid != b.reservationid) AND NOT((a.date+a.timeframe_end*slotLength*" + PageUtils.MS_IN_MIN +" <= b.date+b.timeframe_start*slotLength*" + PageUtils.MS_IN_MIN +") OR (a.date+a.timeframe_start*slotLength*" + PageUtils.MS_IN_MIN +" >= b.date+b.timeframe_end*slotLength*" + PageUtils.MS_IN_MIN +")))) fish INTO @tmp; " +
				"IF @tmp > 0 THEN " +
				"SELECT @tmp; ROLLBACK; " +
				"ELSE " +
				"SELECT count(*) FROM user_dbusers WHERE username=username AND usernumber=usernumber INTO @tmp; " +
				"IF @tmp=0 THEN " +
				"SELECT -1; ROLLBACK; " +
				"ELSE " +
				"SELECT 0; COMMIT; " +
				"END IF; END IF; " +
				"END");
		
		// Create procedure to delete a user from the database
		execute("DROP procedure IF EXISTS `deleteUser`");
		execute("CREATE PROCEDURE `deleteUser`(IN number INT, IN name VARCHAR(10), IN timenow BIGINT) " +
				"BEGIN " +
				"SELECT count(*) FROM user_dbusers WHERE username=name AND usernumber=number INTO @count FOR UPDATE; " +
				"IF @count=1 THEN " +
				"UPDATE user_dbusers SET usernumber=(-1-number) WHERE username=name AND usernumber=number; " +
				"UPDATE instrument_info, user_info, reservations, reservations_archive SET total_slots=@tmp WHERE user_info.username=name AND user_info.username=reservations.username AND user_info.groupname=reservations_archive.groupname AND reservations_archive.instrumentid=instrument_info.instrumentid AND reservations_archive.instrumentid=reservations.instrumentid AND timenow>=reservations.date+reservations.timeframe_start*instrument_info.timeslotlength*"+PageUtils.MS_IN_MIN+" AND @tmp:=total_slots+(SELECT SUM(timeframe_end-timeframe_start) FROM reservations a WHERE user_info.username=a.username AND reservations_archive.instrumentid=a.instrumentid AND timenow>=a.date+a.timeframe_start*timeslotlength*"+PageUtils.MS_IN_MIN+" FOR UPDATE); " +
				"DELETE reservations FROM reservations INNER JOIN instrument_info WHERE username=name AND instrument_info.instrumentid=reservations.instrumentid AND timenow>=date+timeframe_end*timeslotlength*"+PageUtils.MS_IN_MIN+"; " +
				"DELETE user_dbusers, user_info, user_permissions, user_personalinfo, reservations FROM user_dbusers INNER JOIN user_info INNER JOIN user_permissions INNER JOIN user_personalinfo INNER JOIN reservations WHERE user_dbusers.usernumber=(-1-number) AND user_dbusers.username=user_info.username AND user_dbusers.username=user_permissions.username AND user_dbusers.username=user_personalinfo.username AND user_dbusers.username=reservations.username; " +
				"END IF; END");
		
		// Create procedure to archive old reservations
		execute("DROP procedure IF EXISTS `archiveReservations`");
		execute("CREATE PROCEDURE `archiveReservations`(IN timenow BIGINT) " +
				"BEGIN " +
				"UPDATE instrument_info, user_info, reservations, reservations_archive SET total_slots=@tmp WHERE user_info.username=reservations.username AND user_info.groupname=reservations_archive.groupname AND reservations_archive.instrumentid=instrument_info.instrumentid AND reservations_archive.instrumentid=reservations.instrumentid AND timenow>=reservations.date+reservations.timeframe_end*instrument_info.timeslotlength*"+PageUtils.MS_IN_MIN+" AND @tmp:=total_slots+(SELECT SUM(timeframe_end-timeframe_start) FROM reservations a WHERE user_info.username=a.username AND reservations_archive.instrumentid=a.instrumentid AND timenow>=a.date+a.timeframe_end*timeslotlength*"+PageUtils.MS_IN_MIN+" FOR UPDATE); " +
				"DELETE reservations FROM reservations INNER JOIN instrument_info WHERE instrument_info.instrumentid=reservations.instrumentid AND timenow>=date+timeframe_end*timeslotlength*"+PageUtils.MS_IN_MIN+"; " +
				"END");
		
	}

	/*
	 * Creates Cache and Log tables used for Cacheing pages and logging
	 * all the requests that arrive to the proxy
	 * @throws SQLException
	 */
	private void createApplicationTables () throws SQLException {
		// Create users table with passwords,active
		execute("CREATE TABLE IF NOT EXISTS user_dbusers " +
				"(usernumber INT NOT NULL," +
				" username VARCHAR(10) NOT NULL," +
				" PRIMARY KEY (username) ," +
				" password VARCHAR(16) NOT NULL, " +
				" role VARCHAR(7) DEFAULT 'regular', " +  // types= regular, manager
				" active TINYINT DEFAULT 1," + // 0= not active, 1=active
				" INDEX user_dbusers_idx (username ASC)) ENGINE=innodb");
		// Create user info table
		execute("CREATE TABLE IF NOT EXISTS user_personalinfo " +
				"(username VARCHAR(10) NOT NULL," +
				" PRIMARY KEY (username) ," +
				" phonenum VARCHAR(16) NOT NULL, " +
				" address VARCHAR(50) ," + 
				" INDEX `user_personalinfo_idx` (username ASC)) ENGINE=innodb");
		// Create user permissions table
		execute("CREATE TABLE IF NOT EXISTS user_permissions " +
				"(username VARCHAR(10) NOT NULL," +
				" permission SMALLINT NOT NULL," +
				" INDEX `user_permissions_idx` (username ASC)) ENGINE=innodb");
		// Create user info,group table
		execute("CREATE TABLE IF NOT EXISTS user_info " +
				"(username VARCHAR(10) NOT NULL," +
				" PRIMARY KEY (username) ," +
				" fullname VARCHAR(50) NOT NULL, " +
				" groupname VARCHAR(50) DEFAULT 'lab user'," +
				" INDEX `user_info_idx` (username ASC)) ENGINE=innodb");

		// Create instruments info table
		execute("CREATE TABLE IF NOT EXISTS instrument_info " +
				"(instrumentid INT AUTO_INCREMENT NOT NULL PRIMARY KEY," +
				" type VARCHAR(20) NOT NULL, " +
				" timeslotlength INT NOT NULL," +
				" timeslotsperday INT NOT NULL, " +
				" description VARCHAR(512)," +
				" permission SMALLINT NOT NULL, " +
				" INDEX instrument_info_idx (instrumentid ASC), FULLTEXT(type,description) ) ENGINE=MyISAM");
		
		// Create user instrument reservation
		execute("CREATE TABLE IF NOT EXISTS reservations " +
				"(instrumentid INT NOT NULL," +
				" username VARCHAR(10) NOT NULL, " +
				" reservationid INT AUTO_INCREMENT NOT NULL PRIMARY KEY, " +
				" date BIGINT NOT NULL, " +
				" timeframe_start INT NOT NULL, " +
				" timeframe_end INT NOT NULL, " +
				" INDEX reservations_idx (reservationid ASC)," +
				" INDEX reservations_username_idx (username ASC)," +
				" INDEX reservations_instrumentid_idx (instrumentid ASC)) ENGINE=innodb");
		
		// Create user instrument reservation
		execute("CREATE TABLE IF NOT EXISTS reservations_archive " +
				"(instrumentid INT NOT NULL," +
				" total_slots INT NOT NULL, " +
				" groupname  VARCHAR(25) NOT NULL," +
				" INDEX reservations_archive_instrumentid_idx (instrumentid ASC)) ENGINE=innodb");
		
		// Create groups table
		execute("CREATE TABLE IF NOT EXISTS groups " +
				"(groupname VARCHAR(25) NOT NULL," +
				" PRIMARY KEY (groupname), "+
				" INDEX groups_idx (groupname ASC)) ENGINE=innodb");
		execute("INSERT INTO groups values('lab user')");
	}

	private static String SQLReady(String input){
		return input.replaceAll("\'", "\\\\'").replaceAll("\"", "\\\\\"");
	}

	public Integer retrieveSlotLength(final Integer instrumentid) throws SQLException {
		
		return new Operation<Integer>(){

			@Override
			protected Integer createRetval(ResultSet rs) throws SQLException {
				if (rs.next()){
					return rs.getInt(1);
				}
				else {
					return null;
				}
			}

			@Override
			protected Integer makeQuery() throws SQLException {
				return query("SELECT timeslotlength FROM instrument_info WHERE instrumentid=" + instrumentid);
			}
			
		}.operate();
	}

	public LinkedList<FreeSlot> findFreeSlots(final int instrumentid,
			final long date, final long minutes, final int numOfTimeSlots,
			final Integer slotLength) throws SQLException {
		
		return new Operation<LinkedList<FreeSlot>>(){

			@Override
			protected LinkedList<FreeSlot> createRetval(ResultSet rs)
					throws SQLException {
				long time;
				LinkedList<FreeSlot> retVal = new LinkedList<FreeSlot>();
				while (rs.next()){
					time = rs.getLong("ad") + rs.getInt("ate")*slotLength*PageUtils.MS_IN_MIN;
					retVal.addLast(new FreeSlot(time, rs.getInt("options")));
				}
				return retVal;
			}

			@Override
			protected Integer makeQuery() throws SQLException {
				return query("CALL findFreeSlots(" + instrumentid + ", "
						+ date + ", " + minutes + ", " + numOfTimeSlots + ")");
			}
			
		}.operate();
		
	}

	public LinkedList<Reservation> findReservations(String username) throws SQLException {
		
		if (username == null){
			username = "";
		}
		else {
			username = "AND username='" + SQLReady(username) + "'";
		}
		final String name = username;
		
		return new Operation<LinkedList<Reservation>>(){

			@Override
			protected LinkedList<Reservation> createRetval(ResultSet rs)
					throws SQLException {
				LinkedList<Reservation> retVal = new LinkedList<Reservation>();
				while (rs.next()){
					retVal.addLast(new Reservation(rs.getInt("instrumentid"), rs.getString("username"),
									rs.getInt("reservationid"), rs.getLong("start_time"),rs.getLong("end_time")));
				}
				return retVal;
			}

			@Override
			protected Integer makeQuery() throws SQLException {
				return query("SELECT a.instrumentid, username,reservationid,date+timeframe_start*timeslotlength*"
						+ PageUtils.MS_IN_MIN + " AS start_time, date+timeframe_end*timeslotlength*"
						+ PageUtils.MS_IN_MIN + " AS end_time FROM reservations a, instrument_info b "
						+ "WHERE a.instrumentid=b.instrumentid AND date+timeframe_start*timeslotlength*"
						+ PageUtils.MS_IN_MIN + "> " + System.currentTimeMillis() + " " + name
						+ " ORDER BY instrumentid ASC, start_time ASC");
			}
			
		}.operate();
		
	}
	
	public Boolean isGroup(final String groupname) throws SQLException {
		
		return new Operation<Boolean>(){

			@Override
			protected Boolean createRetval(ResultSet rs) throws SQLException {
				return(rs.next());
			}

			@Override
			protected Integer makeQuery() throws SQLException {
				return query("SELECT groupname FROM groups WHERE groupname='" + SQLReady(groupname)+ "'");
			}
			
		}.operate();
	}
	
	public Integer addInstrument(final String instType, final int instTimeslot,
			final String instDesc, final int instPermission) throws SQLException {
		
		return new Operation<Integer>(){

			@Override
			protected Integer createRetval(ResultSet rs) throws SQLException {
				rs.next();
				return rs.getInt(1);
			}

			@Override
			protected Integer makeQuery() throws SQLException {
				return query("CALL addInstrument('" + SQLReady(instType)+ "'," +
						instTimeslot + ",'" + SQLReady(instDesc) + "'," +
						instPermission +")");
			}
			
		}.operate();
	}
	
	public Integer addUser(final String username, final String password,
			final String fullname, final String phonenum, final String userEmail) throws SQLException {
		
		return new Operation<Integer>(){

			@Override
			protected Integer createRetval(ResultSet rs) throws SQLException {
				rs.next();
				return rs.getInt(1);
			}

			@Override
			protected Integer makeQuery() throws SQLException {
				try{
					return query("CALL addUser('" + SQLReady(username)+ "','" +
						SQLReady(password) + "','" + SQLReady(phonenum) + "','" +
						SQLReady(userEmail) +"','" + SQLReady(fullname)+ "')");
				} catch (SQLException e){
					if (e.getMessage().contains(DUPLICATE_ENTRY)){
						return null;
					}
					else {
						throw e;
					}
				}
			}
			
		}.operate();
	}
	
	public UserDetails getUser(final Integer usernumber) throws SQLException {
		
		return new Operation<UserDetails>(){

			LinkedList<Integer> permissions = null;
			@Override
			protected UserDetails createRetval(ResultSet rs)
					throws SQLException {
				if (rs.next()){
					return new UserDetails(rs.getString("username"), usernumber, rs.getString("fullname"),
							rs.getString("groupname"), rs.getString("phonenum"),
							rs.getString("address"), rs.getBoolean("active"),
							 rs.getString("role").equals("manager"), permissions);
				}
				else{
					return null;
				}
			}

			@Override
			protected Integer makeQuery() throws SQLException {
				permissions = getPermissions(usernumber);
				return query("SELECT c.username,fullname,groupname,phonenum,address,active, role" +
						" FROM user_personalinfo a, user_info b, user_dbusers c" +
						" WHERE c.usernumber="+usernumber+" AND a.username=b.username" +
						" AND b.username = c.username");
			}
			
		}.operate();
	}

	private LinkedList<Integer> getPermissions(final Integer usernumber) throws SQLException {
		
		return new Operation<LinkedList<Integer>>(){

			@Override
			protected LinkedList<Integer> createRetval(ResultSet rs)
					throws SQLException {
				LinkedList<Integer> permissions = new LinkedList<Integer>();
				while (rs.next()){
					permissions.add(rs.getInt("permission"));
				}
				return permissions;
			}

			@Override
			protected Integer makeQuery() throws SQLException {
				return query("SELECT permission FROM user_permissions a, user_dbusers b"
						+ " WHERE b.usernumber=" + usernumber +" AND a.username=b.username"
						+ " order by permission asc");
			}
			
		}.operate();
	}
	
	public ReturnValues makeReservation(final String userName, final Integer usernumber,
			final Integer instId, final long timeInMillis, final int i,
			final Integer numOfTimeSlots) throws SQLException {
		
		return new Operation<ReturnValues>(){

			@Override
			protected ReturnValues createRetval(ResultSet rs)
					throws SQLException {
				rs.next();
				if(rs.getInt(1) != 0){
					return ReturnValues.FAILURE;
				}
				else {
					return ReturnValues.SUCCESS;
				}
			}

			@Override
			protected Integer makeQuery() throws SQLException {
				return query("CALL makeReservation(" + instId +
						"," + usernumber + ",'" + userName + "', " + timeInMillis +
						", " + i + "," + numOfTimeSlots +")");
			}
			
		}.operate();
	}
	
	public Integer getUserNumber(final String username, final String password) throws SQLException {
		
		return new Operation<Integer>(){

			@Override
			protected Integer createRetval(ResultSet rs) throws SQLException {
				if (rs.next()){
					return rs.getInt(1);
				}
				else {
					return -1;
				}
			}

			@Override
			protected Integer makeQuery() throws SQLException {
				return query("SELECT usernumber FROM user_dbusers"
						+ " WHERE username='" + SQLReady(username)
						+ "' AND password='" + password+ "'");
			}
			
		}.operate();
	}

	public Set<String> getGroups() throws SQLException {
		
		return new Operation<Set<String>>(){

			@Override
			protected Set<String> createRetval(ResultSet rs)
					throws SQLException {
				Set<String> retVal = new HashSet<String>();
				while (rs.next()){
					retVal.add(rs.getString("groupname"));
				}
				return retVal;
			}

			@Override
			protected Integer makeQuery() throws SQLException {
				return query("SELECT groupname FROM groups ORDER BY groupname");
			}
			
		}.operate();
	}	

	public LinkedList<UserDetails> getAllUsers() throws SQLException {
		
		return new Operation<LinkedList<UserDetails>>(){

			@Override
			protected LinkedList<UserDetails> createRetval(ResultSet rs)
					throws SQLException {
				LinkedList<UserDetails> retVal = new LinkedList<UserDetails>();
				while (rs.next()){
					Integer usernumber = rs.getInt("usernumber");
					LinkedList<Integer> permissions = getPermissions(usernumber);
					if (permissions == null){
						return null;
					}
					else {
						retVal.add(new UserDetails(rs.getString("username"),
													usernumber,
													rs.getString("fullname"),
													rs.getString("groupname"),
													rs.getString("phonenum"),
													rs.getString("address"),
													rs.getBoolean("active"),
													rs.getString("role").equals("manager")?true:false,
													permissions));
					}
				}
				return retVal;
			}

			@Override
			protected Integer makeQuery() throws SQLException {
				return query("SELECT a.username,usernumber,fullname,role,active,phonenum,groupname,address,photo"
						+ " FROM user_dbusers a, user_info b, user_personalinfo c"
						+ " WHERE a.username=b.username AND b.username=c.username");
			}
			
		}.operate();
	}
	
	public LinkedList<Instrument> getPermitedInstruments(final Integer usernumber) throws SQLException {
		
		return new Operation<LinkedList<Instrument>>(){

			@Override
			protected LinkedList<Instrument> createRetval(ResultSet rs)
					throws SQLException {
				LinkedList<Instrument> retVal = new LinkedList<Instrument>();
				while (rs.next()){
					retVal.add(new Instrument(rs.getString("type"),
												rs.getString("description"),
												rs.getInt("instrumentid"),
												rs.getInt("timeslotlength"),
												rs.getInt("permission")));
				}
				return retVal;
			}

			@Override
			protected Integer makeQuery() throws SQLException {
				return query("SELECT type,instrumentid,timeslotlength,description, a.permission"
						+ " FROM instrument_info a,user_permissions b ,user_dbusers c"
						+ " WHERE c.usernumber=" + usernumber +" AND b.username=c.username AND a.permission=b.permission"
							+ " order by type asc ");
			}
			
		}.operate();
	}
	
	public String getPassword(final String username, final String email) throws SQLException {
		
		return new Operation<String>() {

			@Override
			protected String createRetval(ResultSet rs) throws SQLException {
				if (rs.next()){
					return rs.getString(1);
				}
				else {
					return "";
				}
			}

			@Override
			protected Integer makeQuery() throws SQLException {
				return query("SELECT password"
						+ " FROM user_dbusers a, user_personalinfo b"
						+ " WHERE a.username='" + SQLReady(username) +"'"
						+ " AND a.username=b.username AND b.address='" + SQLReady(email) + "'");
			}
		}.operate();
	}
	
	public String[][] getTotalReservations() throws SQLException{
		
		return new Operation<String[][]>(){

			@Override
			protected String[][] createRetval(ResultSet rs) throws SQLException {
				int counter = 0;
				while (rs.next()){
					++counter;
				}
				String[][] retVal = new String[counter][3];
				rs.beforeFirst();
				counter = 0;
				while (rs.next()){
					retVal[counter][0]=rs.getString("type") + "-" + String.valueOf(rs.getInt("instrumentid"));
					retVal[counter][1]=rs.getString("groupname");
					retVal[counter][2]=String.valueOf(rs.getInt("total_slots"));
					++counter;
				}
				return retVal;
			}

			@Override
			protected Integer makeQuery() throws SQLException {
				return query("SELECT instrumentid, groupname, total_slots, type FROM (SELECT instrumentid, groupname, total as total_slots, type, @num:=if(@typeaux = groupname, @num + 1, 1) as rowcount, @typeaux:=groupname as dummy FROM (SELECT instrumentid, groupname, total_slots, type, @tot:=if(@type = groupname, @tot + total_slots, total_slots) as total, @type:=groupname as dummy FROM " +
						"((SELECT a.instrumentid, groupname, total_slots, b.type FROM reservations_archive a, instrument_info b WHERE a.instrumentid=b.instrumentid AND @tot:=1 AND @num:=1 AND @type:='1' AND @typeaux:='1') " +
						" UNION ALL " +
						"(SELECT a.instrumentid, groupname, timeframe_end-timeframe_start as total_slots, type FROM instrument_info a, reservations b, user_info c WHERE a.instrumentid=b.instrumentid AND b.username=c.username) ORDER BY instrumentid ASC, groupname ASC) as x ORDER BY instrumentid ASC, groupname ASC, total_slots DESC) AS bef ORDER BY instrumentid ASC, groupname ASC) AS yy WHERE yy.rowcount=1");
			}
			
		}.operate();
	}
	
	public LinkedList<Instrument> searchInstruments(final Integer usernumber,
			String name, String searchQ) throws SQLException {
		
		String[] subqueryAux = searchQ.split("[ \\?\\!,;:/\\.\\\\]");
		final String searchQuery = SQLReady(searchQ);
		String subq1 = "";
		String subq2 = "";
		for (int i=0;  i<subqueryAux.length; ++i){
			subq1 += "+"+SQLReady(subqueryAux[i])+" ";
			subq2 += SQLReady(subqueryAux[i])+"* ";
		}
		final String subquery1 = subq1;
		final String subquery2 = subq2;
		final String username = SQLReady(name);
		
		return new Operation<LinkedList<Instrument>>() {

			@Override
			protected LinkedList<Instrument> createRetval(ResultSet rs)
					throws SQLException {
				LinkedList<Instrument> retVal = new LinkedList<Instrument>();
				while (rs.next()){
					retVal.add(new Instrument(rs.getString("type"), rs.getString("description"), rs.getInt("instrumentid"), rs.getInt("timeslotlength"),0));
				}
				return retVal;
			}

			@Override
			protected Integer makeQuery() throws SQLException {
				return query("SELECT type,description,instrumentid,timeslotlength FROM " +
						"(SELECT type,description,instrumentid,timeslotlength,prime,second, @tmp:=IF(@type=instrumentid,@tmp+1,1) AS aux, @type:= instrumentid AS dummy FROM " +
						"((SELECT inf.type, inf.description, inf.instrumentid, inf.timeslotlength, 0 AS prime, MATCH(inf.type,inf.description) AGAINST('\""+searchQuery+"\"' IN BOOLEAN MODE) AS second FROM instrument_info inf, user_dbusers db,  user_permissions per WHERE db.username='"+ username +"' AND db.usernumber="+usernumber+" AND per.username=db.username AND inf.permission=per.permission AND MATCH(inf.type,inf.description) AGAINST('\""+searchQuery+"\"' IN BOOLEAN MODE))" +
						" UNION " +
						"(SELECT inf.type, inf.description, inf.instrumentid, inf.timeslotlength, 1 AS prime, MATCH(inf.type,inf.description) AGAINST('"+subquery1+"' IN BOOLEAN MODE) AS second FROM instrument_info inf, user_dbusers db, user_permissions per WHERE db.username='"+username+"' AND db.usernumber="+usernumber+" AND per.username=db.username AND inf.permission=per.permission AND MATCH(inf.type,inf.description) AGAINST('"+subquery1+"' IN BOOLEAN MODE))" +
						" UNION " +
						"(SELECT inf.type, inf.description, inf.instrumentid, inf.timeslotlength, 2 AS prime, MATCH(inf.type,inf.description) AGAINST('"+subquery2+"' IN BOOLEAN MODE) AS second FROM instrument_info inf, user_dbusers db, user_permissions per WHERE db.username='"+ username +"' AND db.usernumber="+usernumber+" AND per.username=db.username AND inf.permission=per.permission AND MATCH(inf.type,inf.description) AGAINST('"+subquery2+"' IN BOOLEAN MODE) LIMIT "+Math.max(1, (13-searchQuery.length()/2))+")" +
						" UNION " +
						"(SELECT inf.type, inf.description, inf.instrumentid, inf.timeslotlength, 3 AS prime, MATCH(inf.type,inf.description) AGAINST('\""+searchQuery+"\"' IN BOOLEAN MODE) AS second FROM instrument_info inf, user_dbusers db WHERE db.username='"+ username +"' AND db.usernumber="+usernumber+" AND MATCH(inf.type,inf.description) AGAINST('\""+searchQuery+"\"' IN BOOLEAN MODE))" +
						" UNION " +
						"(SELECT inf.type, inf.description, inf.instrumentid, inf.timeslotlength, 4 AS prime, MATCH(inf.type,inf.description) AGAINST('"+subquery1+"' IN BOOLEAN MODE) AS second FROM instrument_info inf, user_dbusers db WHERE db.username='"+ username +"' AND db.usernumber="+usernumber+" AND MATCH(inf.type,inf.description) AGAINST('"+subquery1+"' IN BOOLEAN MODE))" +
						" UNION " +
						"(SELECT inf.type, inf.description, inf.instrumentid, inf.timeslotlength, 5 AS prime, MATCH(inf.type,inf.description) AGAINST('"+subquery2+"' IN BOOLEAN MODE) AS second FROM instrument_info inf, user_dbusers db WHERE db.username='"+ username +"' AND db.usernumber="+usernumber+" AND @tmp:=0 AND @type:=-1 AND MATCH(inf.type,inf.description) AGAINST('"+subquery2+"' IN BOOLEAN MODE) LIMIT "+Math.max(1, (13-searchQuery.length()/3))+")" +
						") choosefrom ORDER BY instrumentid ASC, prime ASC" +
						") base WHERE aux=1 ORDER BY prime ASC, second DESC");
			}
		}.operate();
	}

	/* Executions */
	
	public ReturnValues addGroup(String groupName) throws SQLException {
		
		try {
			execute("CALL addGroup('"+SQLReady(groupName)+"')");
			return ReturnValues.SUCCESS;
		} catch (SQLException e) {
			if (e.getMessage().contains(DUPLICATE_ENTRY)){
				return ReturnValues.FAILURE;
			}
			else if (e.getMessage().contains(SERVER_RESTARTED)){
				return instance.addGroup(groupName);
			}
			else {
				throw e;
			}
		}
	}

	public ReturnValues changeUserPassword(String username, String password,
			int usernumber) throws SQLException {
		
		
		try {
			execute("UPDATE user_dbusers SET password='" + SQLReady(password) + 
					"' where username='" + SQLReady(username)+"' AND usernumber=" + usernumber);
			return ReturnValues.SUCCESS;
		} catch (SQLException e) {
			if (e.getMessage().contains(SERVER_RESTARTED)){
				return instance.changeUserPassword(username,password,usernumber);
			}
			else {
				throw e;
			}
		}
	}

	public ReturnValues deleteUser(final String username, final Integer usernumber) throws SQLException {
		
		try {
			execute("CALL deleteUser("+usernumber+",'"+SQLReady(username)+"',"+System.currentTimeMillis()+")");
			return ReturnValues.SUCCESS;
		} catch (SQLException e) {
			if (e.getMessage().contains(SERVER_RESTARTED)){
				return instance.deleteUser(username,usernumber);
			}
			else {
				throw e;
			}
		}
	}

	public ReturnValues deletReservation(int reservationID, String username) throws SQLException {
		
		try {
			if (username == null){
				username="";
			}
			else {
				username=" AND username='" + SQLReady(username)+"'";
			}
			execute("DELETE FROM reservations WHERE reservationid=" + reservationID + username);
			return ReturnValues.SUCCESS;
		} catch (SQLException e) {
			if (e.getMessage().contains(SERVER_RESTARTED)){
				return instance.deletReservation(reservationID,username);
			}
			else {
				throw e;
			}
		}
	}
	
	public ReturnValues changeName(String username, Integer usernumber, String fullname) throws SQLException {

		return changeInfo(username, usernumber,createCall(username,usernumber,
				"UPDATE user_info SET fullname=\\'" + SQLReady(fullname) +"\\'"));
	}

	public ReturnValues changePhoneNumber(String username, Integer usernumber, String phonenum) throws SQLException {
			
		return changeInfo(username, usernumber,createCall(username,usernumber,
				"UPDATE user_personalinfo SET phonenum=\\'" + SQLReady(phonenum) +"\\'"));
	}

	public ReturnValues changeAddress(String username, Integer usernumber, String email) throws SQLException {
		
		return changeInfo(username, usernumber,createCall(username,usernumber,
				"UPDATE user_personalinfo SET address=\\'" + SQLReady(email) +"\\'"));
	}
	
	public ReturnValues changeActive(String username, Integer usernumber, int active) throws SQLException {
		
		return changeInfo(username, usernumber,
				"UPDATE user_dbusers SET active=" + active + " WHERE usernumber=" + usernumber + "");
	}
	
	public ReturnValues changeGroup(String username, Integer usernumber, String group) throws SQLException {
		
		ReturnValues retval=archiveReservations();
		if ((retval == null) || (retval.equals(ReturnValues.FAILURE))){
			return retval;
		}
		return changeInfo(username, usernumber,createCall(username,usernumber,
				"UPDATE user_info SET groupname=\\'" + SQLReady(group) +"\\'"));
	}
	
	private String createCall(String username, Integer usernumber, String toUpdate){
		return "CALL changeInfo('" +toUpdate +" WHERE username=\\'" +
							SQLReady(username) + "\\'',"+usernumber+")";
	}
	
	public ReturnValues addPermission(String username, Integer usernumber, int per) throws SQLException {
		
		return changeInfo(username, usernumber,
				"CALL addPermission('"+SQLReady(username)+"',"+usernumber+","+per+")");
	}
	public ReturnValues removePermission(String username, Integer usernumber, int per) throws SQLException {
		
		return changeInfo(username, usernumber,
				"CALL removePermission('"+SQLReady(username)+"',"+usernumber+","+per+")");
	}
	
	public ReturnValues promoteUser(String username, Integer usernumber) throws SQLException {
		
		return changeInfo(username, usernumber,
				"UPDATE user_dbusers SET role='manager'"
				+ " WHERE username='" + SQLReady(username) + "' AND usernumber=" + usernumber);
	}
	
	private ReturnValues changeInfo(final String username,
			final Integer usernumber,final String executeCmd) throws SQLException{
		
		try {
			execute(executeCmd);
		}
		catch (SQLException e) {
			if (e.getMessage().contains(SERVER_RESTARTED)){
				return instance.changeInfo(username,usernumber,executeCmd);
			}
			else {
				throw e;
			}
		}
		
		return new Operation<ReturnValues>(){

			@Override
			protected ReturnValues createRetval(ResultSet rs)
					throws SQLException {
				if (rs.next()){
					return ReturnValues.SUCCESS;
				}
				else {
					return ReturnValues.FAILURE;
				}
			}

			@Override
			protected Integer makeQuery() throws SQLException {
				return query("SELECT username FROM user_dbusers "
						+ " WHERE usernumber="+usernumber+" AND username='"+username+"'");
			}
			
		}.operate();
	}

	
	
	public ReturnValues archiveReservations() throws SQLException {
		try {
			execute("CALL archiveReservations("+System.currentTimeMillis()+")");
			return ReturnValues.SUCCESS;
		} catch (SQLException e) {
			if (e.getMessage().contains(SERVER_RESTARTED)){
				return instance.archiveReservations();
			}
			else {
				throw e;
			}
		}
	}
	
	
	
	
	private abstract class Operation<T>{
		
		public T operate() throws SQLException{
			Integer mapNum = -1;
			try {
				mapNum = makeQuery();
				if (mapNum == null){
					return null;
				}
			} catch (SQLException e){
				if (e.getMessage().equals(SERVER_RESTARTED)){
					return operate();
				}
				else{
					throw e;
				}
			}
			ResultSet rs = mapping.get(mapNum);
			if (rs == null){
				mapping.remove(mapNum);
				cons[mapNum] = null;
				throw new SQLException(DATABASE_UNAVAILABLE);
			}
			else {
				return close(createRetval(rs),mapNum);
			}
		}
		
		private T close(T in, Integer mapNum) throws SQLException{
			mapping.get(mapNum).close();
			mapping.remove(mapNum);
			available[mapNum] = true;
			return in;
		}
		
		protected abstract Integer makeQuery() throws SQLException;
		protected abstract T createRetval(ResultSet rs) throws SQLException;
	}
}
