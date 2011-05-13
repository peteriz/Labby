package utilities;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionUtils {

	private static final int TIME_OUT =10*60*1000;
	
	private static HashMap<Integer, String> newUserMapping;
	private static HashMap<Integer, String> oldUserMapping;
	
	static {
		
		newUserMapping = new HashMap<Integer, String>();
		oldUserMapping = new HashMap<Integer, String>();
		
		int delays = SessionUtils.TIME_OUT;
		Timer cleaner = new Timer(true);
		cleaner.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				garbageCollection();
			}
		}, delays, delays);
	}
	
	private static synchronized void garbageCollection(){
		
		HashMap<Integer, String> holder = newUserMapping;
		newUserMapping = oldUserMapping;
		oldUserMapping  = holder;
		newUserMapping.clear();
	}
	
	public static synchronized void addSession(HttpSession session){
		
		String newID =  session.getId();
		Integer usernumber = Integer.valueOf(session.getAttribute("usernumber").toString());
		newUserMapping.put(usernumber, newID);
		oldUserMapping.remove(usernumber);
	}
	
	public static synchronized boolean removeSession(HttpSession session){
		
		try{
			Integer usernumber = Integer.valueOf(session.getAttribute("usernumber").toString());
			String toCheck = session.getId();
			String tmp = newUserMapping.get(usernumber);
			if (tmp != null){
				if (tmp.equals(toCheck)){
					newUserMapping.remove(usernumber);
					return true;
				}
				return false;
			}
			tmp = oldUserMapping.get(usernumber);
			if (tmp != null){
				if (tmp.equals(toCheck)){
					oldUserMapping.remove(usernumber);
					return true;
				}
				return false;
			}
		} catch (Exception e){}
		return false;
	}
	
	private static synchronized boolean containsSession(HttpSession session){
		
		try{
			Integer usernumber = Integer.valueOf(session.getAttribute("usernumber").toString());
			String toCheck = session.getId();
			String tmp = newUserMapping.get(usernumber);
			if (tmp != null){
				return tmp.equals(toCheck);
			}
			tmp = oldUserMapping.get(usernumber);
			if (tmp != null){
				if (tmp.equals(toCheck)){
					newUserMapping.put(usernumber, oldUserMapping.remove(usernumber));
					return true;
				}
				return false;
			}
		} catch (Exception e){}
		return false;
	}
	
	public static boolean checkRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		HttpSession thisSession = request.getSession();
		if (thisSession.getLastAccessedTime()+TIME_OUT <= System.currentTimeMillis()){
			removeSession(thisSession);
		}
		else if (containsSession(thisSession)) {
			response.sendRedirect("main.jsp");
			return true;
		}
		return false;
	}
	
	public static boolean checkLiveSession (HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		HttpSession thisSession = request.getSession();
		
		try{
			if (!containsSession(thisSession)) {
				response.sendRedirect("index.jsp");
				return false;
			}
			else if (thisSession.getLastAccessedTime()+TIME_OUT <= System.currentTimeMillis()){
				removeSession(thisSession);
				response.sendRedirect("index.jsp");
				return false;
			}
		}	catch (Exception e){
			response.sendRedirect("index.jsp");
			return false;
		}
		return true;
	}
	
	public static synchronized void removeUser(Integer usernumber){
		
		String tmp = newUserMapping.remove(usernumber);
		if (tmp == null){
			tmp = oldUserMapping.remove(usernumber);
		}
	}
	
}
