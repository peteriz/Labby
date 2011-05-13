package managers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.smtp.SMTPSSLTransport;

import structures.UserDetails;
import utilities.DatabaseUtils;
import utilities.ServerProperties;
import structures.ReturnValues;

public class UserDetailsManager {

	private static HashMap<Integer,UserDetails> researchers = new HashMap<Integer, UserDetails>();
	private static HashMap<Integer,UserDetails> managers = new HashMap<Integer, UserDetails>();
	private static boolean all = false;
	
	public static final String PHONE_NUMBER_REGEX = "\\d{4,}";
	public static final String EMAIL_REGEX = "([a-zA-Z0-9.]([a-zA-Z0-9-_\\s\\.]*)@[a-zA-Z0-9-\\s]+\\.[a-zA-Z]{2,5}){0,1}";
	
	private static final String[][] protocolInformation = {
		{ "mail.smtps.host", "smtp.gmail.com" },
		{ "mail.smtps.auth", "true" },
		{" mail.transport.protocol", "smtps"} };
	
	public static synchronized LinkedList<UserDetails> getAllUsers() throws SQLException{
		
		if (all){
			LinkedList<UserDetails> retVal = new LinkedList<UserDetails>(researchers.values());
			retVal.addAll(managers.values());
			return retVal;
		}
		else {
			LinkedList<UserDetails> retVal = null;
			try {
				retVal = DatabaseUtils.getInstance().getAllUsers();
			} catch (SQLException e) {
				all = false;
				throw e;
			}
			if (retVal == null){
				return null;
			}
			for (UserDetails user : retVal){
				if (user.isManager()){
					managers.put(user.getUsernumber(), user);
				}
				else{
					researchers.put(user.getUsernumber(), user);
				}
			}
			all=true;
			return retVal;
		}
	}
	
	public static ReturnValues changePassword(String username,
			String password, int usernumber) throws SQLException {
		
		return DatabaseUtils.getInstance().changeUserPassword(username, password, usernumber);
	}

	public static ReturnValues addUser(String username, String password, String fullname,
										String phonenum, String userEmail) throws SQLException{
		
		Integer userID = DatabaseUtils.getInstance().addUser(username, password, fullname, phonenum,userEmail);
		if (userID == null){
			return null;
		}
		else if (userID <0){
			return ReturnValues.FAILURE;
		}
		else {
			addResearcher(new UserDetails(username, userID, fullname, "lab user", phonenum, userEmail, true, false, new LinkedList<Integer>()));
			return ReturnValues.SUCCESS;
		}
	}
	
	private static synchronized void addResearcher(UserDetails user){
		
		if (!(researchers.containsKey(user.getUsernumber()) ||
				managers.containsKey(user.getUsernumber()))){
			researchers.put(user.getUsernumber(), user);
		}
	}
	
	public static Integer getUserNumber(String username, String password) throws SQLException{
		
		return DatabaseUtils.getInstance().getUserNumber(username,password);
	}

	public static UserDetails getUser(Integer usernumber) throws SQLException {
		
		UserDetails user = researchers.get(usernumber);
		if (user != null){
			return user;
		}
		else {
			user = managers.get(usernumber);
			if (user != null){
				return user;
			}
			else {
				user = addUser(usernumber);
				return user;
			}
		}
	}

	private synchronized static UserDetails addUser(Integer usernumber) throws SQLException {
		
		UserDetails user = DatabaseUtils.getInstance().getUser(usernumber);
		if (user != null){
			if (user.isManager()){
				managers.put(usernumber,user);
			}
			else {
				researchers.put(usernumber, user);
			}
		}
		return user;
	}

	public static ReturnValues changeName(String username, Integer usernumber, String fullname) throws SQLException {
		
		ReturnValues retVal = DatabaseUtils.getInstance().changeName(username,usernumber,fullname);
		return finishChange(retVal, usernumber);
	}

	public static ReturnValues changePhoneNumber(String username,
			Integer usernumber, String phonenum) throws SQLException {
		
		ReturnValues retVal = DatabaseUtils.getInstance().changePhoneNumber(username,usernumber,phonenum);
		return finishChange(retVal, usernumber);
	}

	public static ReturnValues changeAddress(String username,
			Integer usernumber, String email) throws SQLException {

		ReturnValues retVal = DatabaseUtils.getInstance().changeAddress(username,usernumber,email);
		return finishChange(retVal, usernumber);
	}
	
	public static ReturnValues changeActive(String username,
					Integer usernumber, int i) throws SQLException {
		ReturnValues retVal = DatabaseUtils.getInstance().changeActive(username,usernumber,i);
		return finishChange(retVal, usernumber);
	}
	
	public static ReturnValues changeGroup(String username, Integer usernumber,
			String group) throws SQLException {
		ReturnValues retVal = DatabaseUtils.getInstance().changeGroup(username,usernumber,group);
		return finishChange(retVal, usernumber);
	}
	
	public static ReturnValues addPermission(String username,
			Integer usernumber, int per) throws SQLException {
		ReturnValues retVal = DatabaseUtils.getInstance().addPermission(username,usernumber,per);
		return finishChange(retVal, usernumber);
	}
	
	public static ReturnValues removePermission(String username,
			Integer usernumber, int per) throws SQLException {
		ReturnValues retVal = DatabaseUtils.getInstance().removePermission(username,usernumber,per);
		return finishChange(retVal, usernumber);
	}
	
	private static ReturnValues finishChange(ReturnValues retVal, Integer usernumber) throws SQLException{
		if (retVal == null){
			return ReturnValues.FAILURE;
		}
		else{
			if (retVal.equals(ReturnValues.SUCCESS)){
				remove(usernumber);
			}
			return retVal;
		}
	}
	
	private synchronized static void remove(Integer usernumber) throws SQLException {
		researchers.remove(usernumber);
		managers.remove(usernumber);
		getUser(usernumber);
	}

	public static ReturnValues promoteUser(String username, Integer usernumber) throws SQLException {
		ReturnValues retVal = DatabaseUtils.getInstance().promoteUser(username, usernumber);
		if (retVal == null){
			return null;
		}
		else if (retVal.equals(ReturnValues.SUCCESS)){
			promote(usernumber);
		}
		return retVal;
	}

	private synchronized static void promote(Integer usernumber) {
		
		UserDetails user = researchers.remove(usernumber);
		if (user != null){
			user.setManager();
			managers.put(usernumber, user);
		}
	}
	
	public static boolean retrievePassword(String username, String email) throws SQLException{
		
		if ((username == null) || (username.length() < 6) || (username.length() > 10)){
			return false;
		}
		else if ((email==null) || !email.matches(EMAIL_REGEX)){
			return false;
		}
		else {
			String password = DatabaseUtils.getInstance().getPassword(username, email);
			if ((password == null) || (password.isEmpty())){
				return false;
			}
			else {
				
				try {
					// Set up the message properties
					final ServerProperties srvrProps = new ServerProperties();
					Authenticator authenticator = new Authenticator() {
						@Override
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(srvrProps.getEmailUserName(),
																srvrProps.getEmailPassword());
						}};
					Properties props = new Properties();
					props.put(protocolInformation[0][0], protocolInformation[0][1]);
					props.put(protocolInformation[1][0], protocolInformation[1][1]);
					props.put(protocolInformation[2][0], protocolInformation[2][1]);
					props.put("mail.user", srvrProps.getEmailPassword());
					props.put("mail.from", srvrProps.getEmailPassword());
					props.put("toAddress", email);
					props.put("subject", "Password retrieved");
					
					// Create message and content
					Session session = Session.getInstance(props, authenticator);
					MimeMessage message = new MimeMessage(session);
					message.setSubject((String) props.get("subject"));
					message.setContent("<p>Your password is " + password + "</p>", "text/html");
					InternetAddress to = new InternetAddress((String) props.get("toAddress"));
					message.addRecipient(Message.RecipientType.TO, to);
					message.setFrom();
					message.saveChanges();
					
					// Send message to user
					SMTPSSLTransport transport = SMTPSSLTransport.class.cast(session.getTransport("smtps"));
					transport.connect();
					transport.sendMessage(message, message.getAllRecipients());
					
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				
			}
		}
	}

	public static ReturnValues deleteUser(String username, Integer usernumber) throws SQLException {
		
		ReturnValues retVal = DatabaseUtils.getInstance().deleteUser(username, usernumber);
		if ((retVal != null) && retVal.equals(ReturnValues.SUCCESS)){
			keepCorrct(usernumber,username);
		}
		return retVal;
	}

	private static synchronized void keepCorrct(Integer usernumber, String username) {
		
		UserDetails user = researchers.remove(usernumber);
		if (user == null){
			user = managers.remove(usernumber);
			if ((user != null) && !user.getUsername().equals(username)){
				managers.put(usernumber,user);
			}
		}
		else if (!user.getUsername().equals(username)){
			researchers.put(usernumber,user);
		}
		
	}
	
}
