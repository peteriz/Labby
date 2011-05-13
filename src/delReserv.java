

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import managers.ReservationManager;

import structures.ReturnValues;

/**
 * Servlet implementation class delReserv
 */
public class delReserv extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public delReserv() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if (utilities.SessionUtils.checkLiveSession(request, response)){
			String userName = request.getSession().getAttribute("userName").toString();
			if (userName != null){
				try{
					Integer reservationID = Integer.parseInt(request.getParameter("reservation"));
					ReturnValues retVal;
	          		if ((request.getSession().getAttribute("userRole") != null) &&
	          				(request.getSession().getAttribute("userRole").toString().equals("manager"))){
	          			retVal = ReservationManager.deletReservation(reservationID);
	          		}
	          		else {
	          			retVal = ReservationManager.deletReservation(reservationID,userName);
	          		}
	          		if (retVal == null){
						response.sendError(500,"Server error");
					}
					else if (retVal.equals(ReturnValues.SUCCESS)){
						response.sendRedirect("deleteReservation.jsp");
					}
					else {
						response.sendError(500,"Server error");
					}
				} catch (Exception e){
					response.sendRedirect("deleteReservation.jsp");
				}
			}
		}
	}

}
