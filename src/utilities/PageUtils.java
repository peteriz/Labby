package utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import managers.InstrumentManager;
import managers.ReservationManager;
import managers.UserDetailsManager;

import structures.FreeSlot;
import structures.Instrument;
import structures.Reservation;
import structures.ReturnValues;
import structures.UserDetails;

public class PageUtils {

	/** How many milis are in a minute */
	public static final long MS_IN_MIN = 1000 * 60;
	/** How many minutes are in a day */
	public static final long MIN_IN_DAY = 60 * 24;
	/** How many milis are in a day */
	public static final long MS_IN_DAY = MS_IN_MIN * MIN_IN_DAY;

	private static Calendar today = null;
	
	public static void serverError(SQLException e, HttpServletResponse response) throws IOException{
		
		PrintWriter out = new PrintWriter(new StringWriter());
		e.printStackTrace(out);
		
		if (e.getMessage().contains(DatabaseUtils.NO_FREE_SOCKETS)){
			response.sendError(503, out.toString());
		}
		else {
			response.sendError(500, out.toString());
		}
	}
	
	private static Calendar today() {
		Calendar now = Calendar.getInstance();
		if ((today == null) ||
				!((today.get(Calendar.YEAR)==now.get(Calendar.YEAR)) &&
						(today.get(Calendar.MONTH)==now.get(Calendar.MONTH))&&
						(today.get(Calendar.DAY_OF_MONTH)==now.get(Calendar.DAY_OF_MONTH)))){
			today = Calendar.getInstance();
			today.setTimeInMillis(0);
			today.set(Calendar.YEAR, now.get(Calendar.YEAR));
			today.set(Calendar.MONTH, now.get(Calendar.MONTH));
			today.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
			today.set(Calendar.HOUR_OF_DAY, 0);
			today.set(Calendar.MINUTE, 0);
		}
		return today;
	}
	
	public static synchronized void createReportInfo(Writer out, HttpServletRequest request, HttpServletResponse response) throws IOException{
		try{
			if (SessionUtils.checkLiveSession(request, response)){
				ReturnValues retVal = ReservationManager.archiveReservations();
				if((retVal != null) && retVal.equals(ReturnValues.SUCCESS)){
					String[][] totals = ReservationManager.getTotalReservations();
					if (totals != null){
						if (totals.length>0){
							generateReservationXML(totals, request.getSession().getServletContext().getRealPath("/"));
							out.write("<form action=\"\">");
							out.write("Select the chart you wish to see: ");
							out.write("<select id=\"inst_selection\" >\n");
							out.write("\t<option value=\"all\" >All instruments</option>\n");
							int counter = 0;
							String last = "";
							for (int i=0; i<totals.length; ++i){
								if (!last.equals(totals[i][0])){
									++counter;
									last = totals[i][0];
									out.write("\t<option value=\""+last+"\" >"+last+"</option>\n");
								}
							}
							int groupnumber=totals.length/counter;
							out.write("</select>\n");
							out.write("<button type=\"button\" onclick=\"onSelection()\">View</button>");
							
							
							out.write("<div style=\"display: none\">");
							out.write("<table id=\"datatable\">\n");
							out.write("<thead>\n");
							out.write("<tr>\n");
							out.write("\t<th></th>\n");
							out.write("\t<th>total reservations</th>\n");
							for (int i=0; i<groupnumber; ++i){
								out.write("\t<td>"+totals[i][1]+"</td>\n");
							}
							out.write("</tr>\n");
							out.write("</thead>\n");
							out.write("<tbody>\n");
							for (int i=0; i<counter;++i){
								out.write("<tr>\n");
								out.write("\t<th>" + totals[i*groupnumber][0] + "</th>\n");
								int total = 0;
								for (int j=0; j<groupnumber; ++j){
									total += Integer.parseInt(totals[i*groupnumber+j][2]);
								}
								out.write("\t<td>"+total+"</td>\n");
								for (int j=0; j<groupnumber; ++j){
									out.write("\t<td>"+totals[i*groupnumber+j][2]+"</td>\n");
								}
								out.write("</tr>\n");
							}
							
							out.write("</tbody>\n");
							out.write("</table>");
							out.write("</div>");
							out.write("</form>");
						}
						else {
							out.write("<p class=\"error\"> There are no instruments in the lab </p>");
						}
					}
					else {
						response.sendError(500, "Server is busy");
					}
				}
				else {
					response.sendError(500, "Server is busy");
				}
			}
		} catch (SQLException e){
			serverError(e,response);
		}
	}
	
	private static void generateReservationXML(String[][] totals, String realPath) {
		FileWriter out = null;
        try {
			out = new FileWriter(realPath + File.separator + "xslt" + File.separator + "reservations.xml",false); 
			out.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
			  		  "<!DOCTYPE reservationlist SYSTEM \"" + realPath + File.separator + "xslt" + File.separator + "labby.dtd\">");
			out.write("<RESERVATIONLIST>\n");
			
			for (int i=0; i<totals.length; ++i){
				String instid=totals[i][0].split("-")[totals[i][0].split("-").length-1];
				
				out.write("\t<RESERVATION>\n");
				out.write("\t\t<INSTRUMENTID>" + instid +  "</INSTRUMENTID>\n");
				out.write("\t\t<INSTRUMENTTYPE>" + totals[i][0].substring(0, totals[i][0].length()-instid.length()-1) +  "</INSTRUMENTTYPE>\n");
				out.write("\t\t<GROUP>" + totals[i][1] +  "</GROUP>\n");
				out.write("\t\t<TIMESLOTS>" + totals[i][2] +  "</TIMESLOTS>\n");
				out.write("\t</RESERVATION>\n");
			}
			out.write("</RESERVATIONLIST>\n");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}  finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}

	public static void createUserInfoTable(Writer out, HttpServletRequest request, HttpServletResponse response) throws IOException{
		try{
			if(SessionUtils.checkLiveSession(request, response)){
	   			String msg = request.getParameter("code");
	   			if ((msg != null) && (msg.equals("ok"))) {
	   				out.write("<p class=\"error\">password changed successfuly!</p>");
	   			}
	   			Integer usernumber = Integer.valueOf(request.getSession().getAttribute("usernumber").toString());
	   			UserDetails user = UserDetailsManager.getUser(usernumber);
	   			if (user == null){
	   				response.sendError(500,"server is busy");
	   			}
	   			else {
	   				out.write("<table class=\"userinfo\">");
	    			out.write("<tr>");
	    			out.write("<td class=\"userinfo_h\">Username:</td>");
	    			out.write("<td id=\"username\">" + user.getUsername() + "</td>");
	    			out.write("</tr>");
	    			out.write("<tr>");
	    			out.write("<td class=\"userinfo_h\">Full name:</td>");
	    			out.write("<td><div id=\"fullname\">" + user.getFullname() + "</div></td>");
	    			out.write("</tr>");
	    			out.write("<tr>");
	    			out.write("<td class=\"userinfo_h\">Group:</td>");
	    			out.write("<td>" + user.getGroupname() + "</td>");
	    			out.write("</tr>");
	    			out.write("<tr>");
	    			out.write("<td class=\"userinfo_h\">Phone no.:</td>");
	    			out.write("<td><div id=\"phonenum\" >" + user.getPhonenum() + "</div></td>");
	    			out.write("</tr>");
	    			out.write("<tr>");
	    			out.write("<td class=\"userinfo_h\">E-Mail</td>");
	    			out.write("<td><div id=\"email\" >" + user.getAddress() + "</div></td>");
	    			out.write("</tr>");
	    			out.write("</table>");
	   			}
			}
		} catch (SQLException e){
			serverError(e,response);
		}
	}
	
	public static void createInstrumentsTable(Writer out, HttpServletRequest request, HttpServletResponse response) throws IOException{
		try{
			if (SessionUtils.checkLiveSession(request, response)){
		      	if (request.getParameter("code") != null) {
		      		if (request.getParameter("code").equals("not_free")){
		          		out.write("<p class=\"error\" >Could not complete the reservation</p>");
		      		}
		      	}
		      	Integer usernumber = Integer.valueOf(request.getSession().getAttribute("usernumber").toString());
				LinkedList<Instrument> instruments = InstrumentManager.getPermitedInstruments(usernumber);
	 			if (instruments == null){
	 				response.sendError(500,"server is busy");
	 			}
	 			else if (instruments.isEmpty()){
	 				out.write("<p class=\"grey\">You do not have permission to reserve any instrument</p>");
	 			}
	 			else {
	 				out.write("<form name=\"reserve\" action=\"findReserve.jsp\"  method=\"get\" onsubmit=\"return checkfields()\" >");
	      			out.write("Select instrument to reserve:<br/>");
	      			out.write("<table class=\"display\" id=\"tablesearch\">");
	      			out.write("<thead>");
	      			out.write("<tr>");
	      			out.write("<th class=\"th10\">ID</th>");
	      			out.write("<th class=\"th15\">Type</th>");
	      			out.write("<th class=\"th15\">Timeslot<div class=\"smaller\">[min]</div></th>");
	      			out.write("<th>Description</th>");
	      			out.write("</tr>");
	      			out.write("</thead>");
	      			out.write("<tbody>");
	      			
	      			String checked = " checked=\"checked\" ";
	      			for (Instrument inst: instruments){
	      				out.write("<tr><td><input type=\"radio\" name=\"selectedInst\" value=\"" + inst.getInstrumentid() + "\" " + checked + " /> " + inst.getInstrumentid() + "</td>" +
	  							"<td>" + inst.getType() + "</td><td>" + inst.getTimeslotlength() + "</td><td>"
	  							+ inst.getDescription() +"</td></tr>");
	      				checked="";
	      			}
	      			out.write("</tbody>");
	      			out.write("</table>");
	      			
	      			out.write("<br/><table>");
	      			out.write("<tr>");
	      			out.write("<td>Select no. of time slots:</td>");
	      			out.write("<td><input type=\"text\" name=\"num_timeslots\" maxlength=\"3\" size=\"8\" id=\"timeslots_field\" /></td>");
	      			out.write("</tr>");
	      			out.write("<tr><td></td>");
	      			out.write("<td><div id=\"timeslots_err\" class=\"error\"></div></td>");
	      			out.write("</tr>");
	      			out.write("<tr>");
	      			out.write("<td>Select the date to search a reservation from:</td>");
	      			out.write("<td><input id=\"datepicker\"  type=\"text\" name=\"date\" /></td>");
	      			out.write("</tr>");
	      			out.write("<tr>");
	      			out.write("<td></td><td><input type=\"submit\" value=\"Find reservation\" /></td>");
	      			out.write("</tr>");
	      			out.write("</table>");
	      			out.write("</form>");
				}
			}
		} catch (SQLException e){
			serverError(e,response);
		}
	}
	
	public static void createPromote(boolean ok, Writer out, HttpServletResponse response) throws IOException{
		try{
			if (ok) {
				LinkedList<UserDetails> users = UserDetailsManager.getAllUsers();
				if (users == null){
					response.sendError(500,"server is busy");
				}
				else {
					int counter = 0;
					for (UserDetails user : users){
						if (user.isActive() && !user.isManager()){
							++counter;
						}
					}
					if (counter == 0){
						out.write("<p>There are no users to promote.</p>");
					}
					else{
						Collections.sort(users);
						out.write("<p>Select a user in the list below to promote to Lab manager:</p>");
	      				out.write("<form name=\"input\" action=\"promoteUser\" method=\"get\" onsubmit=\"setnumber()\">");
	      				out.write("<select name=\"users\">");
	      				for (UserDetails user : users){
							if (user.isActive() && !user.isManager()){
								out.write("<option value=\"" + user.getUsername() + "\">" + user.getUsername() + "</option>");
							}
						}
	      				out.write("</select><br/>");
	      				for (UserDetails user : users){
							if (user.isActive() && !user.isManager()){
								out.write("<input type=\"hidden\" id=\""+user.getUsername()+"\" value=\""+user.getUsernumber()+"\" />");
							}
						}
	      				out.write("<input type=\"hidden\" name=\"usernumber\" />");
	      				out.write("<input type=\"password\" name=\"password\" size=\"30\" /><br/>");
	      				out.write("<input type=\"submit\" value=\"Promote\" />");
	      				out.write("</form>");
					}
				}
			}
			else {
				response.sendRedirect("index.jsp");
			}
		} catch (SQLException e){
			serverError(e,response);
		}
	}
	
	public static void createUsersTable(Writer out, HttpServletRequest request,HttpServletResponse response) throws IOException{
		try{
			if (SessionUtils.checkLiveSession(request, response)){
				Integer usernumber = Integer.valueOf(request.getSession().getAttribute("usernumber").toString());
				LinkedList<UserDetails> users = UserDetailsManager.getAllUsers();
				if (users==null){
					response.sendError(500,"server is busy");
				}
				else{
					Collections.sort(users, new Comparator<UserDetails>() {
		
						@Override
						public int compare(UserDetails o1, UserDetails o2) {
							return o1.getFullname().compareTo(o2.getFullname());
						}
					});
					int countRegulars = 0;
					int countManagers = 0;
					for (UserDetails user : users){
						if (user.isActive()){
							if (user.isManager()){
								++countManagers;
							}
							else {
								++countRegulars;
							}
						}
					}
					if (countManagers==0){
						out.write("<p class=\"grey\">No lab managers in system</p>");
					}
					else {
						out.write("<table id=\"managerTable\" class=\"tablesorter\">");
						out.write("<caption>Lab managers</caption>");
		  				out.write("<thead class=\"widthfix\">");
		  				out.write("<tr>");
		  				out.write("\t<th>Name</th>");
		  				out.write("\t<th>Group</th>");
		  				out.write("\t<th>Phone num.</th>");
		  				out.write("\t<th>E-Mail</th>");
		  				out.write("</tr>");
		  				out.write("</thead>");
		  				out.write("<tbody class=\"widthfix\">");
		  				for (UserDetails user : users){
		  					if (user.isManager() && user.isActive()){
		  						out.write("<tr>");
		  						out.write("\t<td>" + user.getFullname() + "</td>");
		  						out.write("\t<td>" + user.getGroupname() + "</td>");
		  						out.write("\t<td>" + user.getPhonenum() + "</td>");
		  						out.write("\t<td>" + user.getAddress() + "</td>");
		  						out.write("</tr>");
		  					}
		  				}
			  			out.write("</tbody>");
			  			out.write("</table>");
						}
						out.write("<br/>");
						if (countRegulars==0){
							out.write("<p class=\"grey\">No researchers in system</p>");
						}
						else {
						out.write("<table id=\"userTable\" class=\"tablesorter\">");
			  			out.write("<caption>Researchers</caption>");
			  			out.write("<thead class=\"widthfix\">");
			  			out.write("<tr>");
			  			out.write("\t<th>Name</th>");
			  			out.write("\t<th>Group</th>");
			  			out.write("\t<th>Phone num.</th>");
			  			out.write("\t<th>E-Mail</th>");
			  			out.write("</tr>");
			  			out.write("</thead>");
			  			out.write("<tbody class=\"widthfix\">");
			  			for (UserDetails user : users){
			  				if (!user.isManager() && user.isActive()){
			      				out.write("<tr>");
				      			out.write("\t<td>" + user.getFullname() + "</td>");
				      			out.write("\t<td>" + user.getGroupname() + "</td>");
				      			out.write("\t<td>" + user.getPhonenum() + "</td>");
				    			out.write("\t<td>" + user.getAddress() + "</td>");
				    			out.write("</tr>");
			  				}
		  				}
		  				out.write("</tbody>");
		  				out.write("</table>");
					}
				}
			}
		} catch (SQLException e){
			serverError(e,response);
		}
	}
	
	public static void createInstrumentsListTable(Writer out, HttpServletRequest request,HttpServletResponse response) throws IOException{
		try{
			if (SessionUtils.checkLiveSession(request, response)){
				LinkedList<Instrument> instruments = InstrumentManager.getAllInstruments();
	 			if (instruments == null){
	 				response.sendError(500,"server is busy");
	 			}
	 			else if (instruments.isEmpty()){
	 				out.write("<p class=\"grey\">No instruments in the system</p>");
	 			}
	 			else {
	      			out.write("<table id=\"instrumentTable\" class=\"tablesorter\">");
	      			out.write("<thead>");
	      			out.write("<tr>");
	      			out.write("<th>ID</th>");
	      			out.write("<th>Type</th>");
	      			out.write("<th>Timeslot<div class=\"smaller\">[min]</div></th>");
	      			out.write("<th>Description</th>");
	      			out.write("<th>Permission</th>");
	      			out.write("</tr>");
	      			out.write("</thead>");
	      			out.write("<tbody>");
	      			
	      			for (Instrument inst: instruments){
	      				out.write("<tr><td>"+ inst.getInstrumentid() + "</td>" +
	  							"<td>" + inst.getType() + "</td><td>" + inst.getTimeslotlength() + "</td><td>"
	  							+ inst.getDescription() +"</td><td>" +  inst.getPermission() + "</td></tr>");
	      			}
	      			out.write("</tbody>");
	      			out.write("</table>");
	      			
	      			out.write("<br/>");
				}
			}
		} catch (SQLException e){
			serverError(e,response);
		}
	}

	public static void createReservationTable(HttpServletRequest request,
			Writer out) throws ParseException,
			SQLException, IOException {
		
		Integer instId = Integer.valueOf(request.getParameter("selectedInst"));
		Integer numOfTimeSlots = Integer.valueOf(request.getParameter("num_timeslots"));
		Calendar date = Calendar.getInstance();
		date.setTime(DateFormat.getDateInstance().parse(request.getParameter("date")));
		Integer minutes = 0;

		// Get the starting minute for the allowed requests
		Calendar now = Calendar.getInstance();
		if ((date.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR))
				&& (date.get(Calendar.YEAR) == now.get(Calendar.YEAR))) {
			minutes = now.get(Calendar.MINUTE) + 60*now.get(Calendar.HOUR_OF_DAY);
		}

		Integer slotLength = InstrumentManager.getSlotLength(instId);
		if (slotLength == null){
			throw new SQLException("Possible DB connection error");
		}
		LinkedList<FreeSlot> freeSlots;
		if (date.after(now)){
			freeSlots = ReservationManager.findFreeSlots(instId,
							date.getTimeInMillis()-MS_IN_DAY,
								MIN_IN_DAY-slotLength, numOfTimeSlots);
		}
		else {
			freeSlots = ReservationManager.findFreeSlots(instId,
					date.getTimeInMillis(),minutes, numOfTimeSlots);
		}
		if (freeSlots == null){
			throw new SQLException("Possible DB connection error");
		}
		
		out.write("<form name=\"reserveTime\" action=\"saveReserv\" method=\"post\" >");
		out.write("<input type=\"hidden\" name=\"numOfTimeSlots\" value=\""
				+ numOfTimeSlots + "\" /> ");
		out.write("<input type=\"hidden\" name=\"instId\" value=\"" + instId
				+ "\" /> ");
		long slot = 1 + minutes / slotLength;
		now.setTimeInMillis(date.getTimeInMillis() + slot * slotLength
				* PageUtils.MS_IN_MIN);
		Calendar tmp;
		// Create the free choice only
		if (freeSlots.size() <= 1) {
			if (freeSlots.size() == 1) {
				tmp = freeSlots.getFirst().getCal();
				System.out.println(tmp.getTime().toString());
				System.out.println(now.getTime().toString());
				if (date.after(tmp)){
					tmp = date;
				}
				else if (Calendar.getInstance().after(tmp)) {
					tmp = now;
				}
			} else {
				tmp = now;
			}
			out.write("<input type=\"hidden\" name=\"slot_pick\" value=\"2\" /> ");

		} 
		// Create the selection table before the free choice
		else {
			// Create the selection table
			out.write("<div class=\"bigger\"><input type=\"radio\" name=\"slot_pick\" value=\"1\" checked=\"checked\" /> Choose from the following times</div>");
			out.write("<br/><table class=\"display\" id=\"tablesearch\">");
			out.write("<thead>");
			out.write("<tr>");
			out.write("\t<th>Select</th>");
			out.write("\t<th>Date</th>");
			out.write("\t<th>Start time</th>");
			out.write("</tr>");
			out.write("</thead>");
			out.write("<tbody>");
			String checked = "checked=\"checked\" ";
			int maxOptions = 100;
			for (int i=0; i<freeSlots.size()-1;++i){
				tmp = freeSlots.get(i).getCal();
				int options = freeSlots.get(i).getOptions();
				if (now.getTimeInMillis() > tmp.getTimeInMillis()) {
					tmp.setTimeInMillis(now.getTimeInMillis());
					--options;
				}
				// Create the rows for a given start time
				long time = tmp.getTimeInMillis();
				for (int j = Math.min(maxOptions, options); j > 0; --j) {
					out.write("<tr>");
					out.write("\t<td><input type=\"radio\" name=\"time_pick\" " + checked + "value=\""
							+ tmp.getTimeInMillis() + "\" /></td>");
					checked = "";
					String date2 = getDate(tmp);
					String time2 = getTime(tmp);
					
					out.write("\t<td>" + date2 + "</td>");
					out.write("\t<td>" + time2 + "</td>");
					out.write("</tr>");
					time += slotLength * PageUtils.MS_IN_MIN;
					tmp.setTimeInMillis(time);
					--maxOptions;
				}
			}
			out.write("</tbody>");
			out.write("</table>");

			// Create the freestyle choice
			tmp = freeSlots.getLast().getCal();
			out.write("<br/><br/>");
			out.write("<div  class=\"bigger\"><input type=\"radio\" name=\"slot_pick\" value=\"2\" />Select from free time</div>");
		}
		// Create the freestyle area
		String date2 = getDate(tmp);
		String time2 = getTime(tmp);
		// Create the time and date selectors
		Integer base =Integer.parseInt(time2.split(":")[0])*60+Integer.parseInt(time2.split(":")[1]); 
		if (base%slotLength!=0){
			base+=slotLength-(base%slotLength);
			tmp = today();
			tmp.set(Calendar.HOUR_OF_DAY, base/60);
			tmp.set(Calendar.MINUTE, base%60);
			time2 = getTime(tmp);
		}
		out.write("<p class=\"grey\">Choose any time you want, starting from " + date2 + " at " +time2 + ": </p>" );
		out.write("<input id=\"datepicker\" type=\"text\" name=\"date\" /> ");
		out.write("<input id=\"hidden\" type=\"hidden\" value=\"" + date2 + "\" /> ");
		out.write("<select name=\"hour\">");

		for (minutes = 0; minutes < PageUtils.MIN_IN_DAY; minutes += slotLength) {
			tmp = today();
			tmp.set(Calendar.HOUR_OF_DAY, minutes/60);
			tmp.set(Calendar.MINUTE, minutes%60);
			time2 = getTime(tmp);
			out.write("<option value=\"" + time2 + "\">" + time2 + "</option>");
		}
		out.write("</select>");
		out.write("<input type=\"submit\" value=\"Save reservation\" />");
		out.write("</form>");

	}

	private static String getDate(Calendar tmp) {
		
		String day = Integer.toString(tmp.get(Calendar.DAY_OF_MONTH));
		if (day.length() < 2) {
			day = "0" + day;
		}
		String month = Integer.toString(1 + tmp.get(Calendar.MONTH));
		if (month.length() < 2) {
			month = "0" + month;
		}
		return day+"/"+month+"/"+Integer.toString(tmp.get(Calendar.YEAR));
	}

	private static String getTime(Calendar tmp) {
		
		String hour = Integer.toString(tmp.get(Calendar.HOUR_OF_DAY));
		if (hour.length() < 2) {
			hour = "0" + hour;
		}
		String min = Integer.toString(tmp.get(Calendar.MINUTE));
		if (min.length() < 2) {
			min = "0" + min;
		}
		return hour+":"+min;
	}

	public static void createManagerDeleteTable(Writer out)
			throws SQLException, IOException {

		LinkedList<Reservation> reservations = ReservationManager.findReservations();
		if (reservations == null){
			throw new SQLException("Possible DB connection error");
		}

		if (reservations.isEmpty()){
			out.write("<p class=\"grey\">No reservations available</p>");
		} else {
			out.write("<form name=\"deletion\" action=\"delReserv\" method=\"post\" >");
			out.write("<table class=\"display\" id=\"tablesearch\">");
			out.write("<thead>");
			out.write("<tr>");
			out.write("\t<th>Reservation ID</th>");
			out.write("\t<th>Instrument ID</th>");
			out.write("\t<th>Reserved by</th>");
			out.write("\t<th>Start time</th>");
			out.write("\t<th>End time</th>");
			out.write("</tr>");
			out.write("</thead>");
			out.write("<tbody>");
			for (Reservation reserv : reservations) {
				out.write("<tr>");
				out.write("\t<td>" + reserv.getReservationid() + "</td>");
				out.write("\t<td>" + reserv.getInstrumentid() + "</td>");
				out.write("\t<td>" + reserv.getUsername() + "</td>");
				printDateCell(reserv.getStart_time(), out);
				printDateCell(reserv.getEnd_time(), out);
				out.write("</tr>");
				
			}
			out.write("</tbody>");
			out.write("</table>");
			out.write("<br/>Select reservation number to remove: <input type=\"text\" name=\"reservation\" maxlength=\"12\" size=\"14\"/> ");
			out.write("<br/><input type=\"submit\" value=\"Delete reservation\" />");
			out.write("</form>");

		}
	}

	public static void createUserDeleteTable(String userName, Writer out)
			throws SQLException, IOException {

		LinkedList<Reservation> reservations = ReservationManager.findReservations(userName);
		if (reservations == null){
			throw new SQLException("Possible DB connection error");
		}

		if (reservations.isEmpty()){
			out.write("<p class=\"grey\">No reservations available</p>");
		} else {
			out.write("<form name=\"deletion\" action=\"delReserv\" method=\"post\" onsubmit=\"return check_fields()\">");
			out.write("<table class=\"display\" id=\"tablesearch\">");
			out.write("<thead>");
			out.write("<tr>");
			out.write("\t<th>Reservation ID</th>");
			out.write("\t<th>Instrument ID</th>");
			out.write("\t<th>Start time</th>");
			out.write("\t<th>End time</th>");
			out.write("</tr>");
			out.write("</thead>");
			out.write("<tbody>");
			for (Reservation reserv : reservations) {
				out.write("<tr>");
				out.write("\t<td>" + reserv.getReservationid() + "</td>");
				out.write("\t<td>" + reserv.getInstrumentid() + "</td>");
				printDateCell(reserv.getStart_time(), out);
				printDateCell(reserv.getEnd_time(), out);
				out.write("</tr>");
				
			}
			out.write("</tbody>");
			out.write("</table>");
			out.write("<br/>Select reservation number to remove: <input type=\"text\" name=\"reservation\" maxlength=\"12\" size=\"14\"/> ");
			out.write("<p class=\"error\" id=\"error_field\"></p>");
			out.write("<br/><input type=\"submit\" value=\"Delete reservation\" />");
			out.write("</form>");
		}
	}

	private static void printDateCell(Calendar cal, Writer out) throws IOException {

		String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
		String month = Integer.toString(1 + cal.get(Calendar.MONTH));
		String year = Integer.toString(cal.get(Calendar.YEAR));
		String hour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
		String min = Integer.toString(cal.get(Calendar.MINUTE));

		if (day.length() < 2) {
			day = "0" + day;
		}
		if (month.length() < 2) {
			month = "0" + month;
		}
		if (hour.length() < 2) {
			hour = "0" + hour;
		}
		if (min.length() < 2) {
			min = "0" + min;
		}

		out.write("\t<td>" + day + "/" + month + "/" + year + " " + hour + ":"
				+ min + "</td>");
	}
	
	public static void printLoggedInAs(Writer out, HttpSession session) throws IOException{
		
		if (session.getAttribute("userName") != null) {
    		out.write("Logged in as " +session.getAttribute("userName"));
    	} else {
    		out.write("Not logged in");
    	}
	}
}
