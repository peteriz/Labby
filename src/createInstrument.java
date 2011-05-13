

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import managers.InstrumentManager;

import utilities.PageUtils;
import utilities.SessionUtils;

/**
 * Servlet implementation class createInstrument
 */
public class createInstrument extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public createInstrument() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Check live session
		if (SessionUtils.checkLiveSession(request, response)){
			String instType = request.getParameter("type");
			Integer instPermission;
			Integer instTimeslot;
			try{
				instPermission = Integer.parseInt(request.getParameter("permission"));
			} catch (Exception e){
				response.sendRedirect("addInstrument.jsp?code=permission");
				return;
			}
			try{
				instTimeslot = Integer.parseInt(request.getParameter("timeslot"));
			} catch (Exception e){
				response.sendRedirect("addInstrument.jsp?code=timeslot");
				return;
			}
			String instDesc = request.getParameter("description");
			
			// Values validation
			if ((instType==null) || (instType.length()< 1) || (instType.length()>20) ) {
				response.sendRedirect("addInstrument.jsp?code=type");
			}
			else if ( (instPermission<0) || (instPermission>=1000)) {
				response.sendRedirect("addInstrument.jsp?code=permission");
			}
			else if ( (instTimeslot<=0) || (instTimeslot>1440)) {
				response.sendRedirect("addInstrument.jsp?code=timeslot");
			}
			else if ( (instDesc==null) || (instDesc.length()>512)) {
				response.sendRedirect("addInstrument.jsp?code=description");
			}
			else {
				try{
					Integer id = InstrumentManager.addInstrument(instType, instTimeslot, instDesc, instPermission);
					if (id == null){
						response.sendError(500);
					}
					else {
						response.sendRedirect("main.jsp?code=instok&id=" + id);
					}
				} catch (SQLException e){
					PageUtils.serverError(e,response);
				}
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
