

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import managers.ReservationManager;
import utilities.SessionUtils;
import structures.ReturnValues;

/**
 * Servlet implementation class saveReserv
 */
public class saveReserv extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public saveReserv() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if (SessionUtils.checkLiveSession(request, response)){
			try{
				String userName = request.getSession().getAttribute("userName").toString();
				Integer usernumber = Integer.valueOf(request.getSession().getAttribute("usernumber").toString());
				Integer instId = Integer.valueOf(request.getParameter("instId"));
				Integer numOfTimeSlots =  Integer.decode(request.getParameter("numOfTimeSlots"));
				Calendar date = Calendar.getInstance();
				Calendar now = Calendar.getInstance();
				Calendar tmp = Calendar.getInstance();
				String[] time = null;
				
				// Set according to free selection
				if (Integer.parseInt(request.getParameter("slot_pick")) == 2){
		 			date.setTime(DateFormat.getDateInstance().parse(request.getParameter("date")));
		 			tmp.setTimeInMillis(date.getTimeInMillis());
		 			time = request.getParameter("hour").split("\\:");
					date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
					date.set(Calendar.MINUTE, Integer.parseInt(time[1]));
				}
				else {
					date.setTimeInMillis(Long.parseLong(request.getParameter("time_pick")));
					tmp.setTimeInMillis(0);
					tmp.set(Calendar.YEAR, date.get(Calendar.YEAR));
					tmp.set(Calendar.MONTH, date.get(Calendar.MONTH));
					tmp.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
					tmp.set(Calendar.HOUR_OF_DAY, 0);
					tmp.set(Calendar.MINUTE, 0);
					time = new String[2];
					time[0] = Integer.toString(date.get(Calendar.HOUR_OF_DAY));
					time[1] = Integer.toString(date.get(Calendar.MINUTE));
				}
	 			
	 			if (numOfTimeSlots<1){
	 				response.sendRedirect("reserve.jsp?code=not_free");
	 			}
	 			else if (date.getTimeInMillis() < now.getTimeInMillis()){
	 				response.sendRedirect("reserve.jsp?code=not_free");
	 			}
	 			else {
	 				ReturnValues retVal = ReservationManager.makeReservation(userName,usernumber,
	 												instId,tmp.getTimeInMillis(),
	 												Integer.parseInt(time[1])+60*Integer.parseInt(time[0]),
	 												numOfTimeSlots);
	 				if (retVal == null){
	 					response.sendError(500, "server is busy");
	 				}
	 				else if (retVal.equals(ReturnValues.SUCCESS)){
	 					response.sendRedirect("main.jsp?code=reservation_done");
	 				}
	 				else {
	 					response.sendRedirect("reserve.jsp?code=not_free");
	 				}
	 			}
			} catch(Exception e){
				response.sendRedirect("reserve.jsp?code=not_free");
			}
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request,response);
	}

}
