

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import managers.UserDetailsManager;

import structures.ReturnValues;
import utilities.PageUtils;
import utilities.SessionUtils;

/**
 * Servlet implementation class UpdateDetail
 */
public class UpdateDetail extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateDetail() {
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
		try{
			if (SessionUtils.checkLiveSession(request, response)){
				String username = request.getSession().getAttribute("userName").toString();
				Integer usernumber = Integer.valueOf(request.getSession().getAttribute("usernumber").toString());
				ReturnValues retVal = null;
				if (request.getParameter("fullname") != null){
					retVal = UserDetailsManager.changeName(username, usernumber, request.getParameter("fullname"));
				}
				else if (request.getParameter("phonenum") != null){
					if (request.getParameter("phonenum").matches(UserDetailsManager.PHONE_NUMBER_REGEX)){
						retVal = UserDetailsManager.changePhoneNumber(username, usernumber, request.getParameter("phonenum"));
					}
				}
				else if (request.getParameter("email") != null){
					if (request.getParameter("email").matches(UserDetailsManager.EMAIL_REGEX)){
						retVal = UserDetailsManager.changeAddress(username, usernumber, request.getParameter("email"));
					}
				}
				if ((retVal == null) || retVal.equals(ReturnValues.FAILURE)){
					response.sendError(500);
				}
			}
		} catch (SQLException e){
			PageUtils.serverError(e,response);
		}
	}
}
