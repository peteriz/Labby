

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import managers.UserDetailsManager;

import utilities.PageUtils;
import utilities.SessionUtils;
import structures.ReturnValues;

/**
 * Servlet implementation class changePassword
 */
public class changePassword extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public changePassword() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			if (SessionUtils.checkLiveSession(request, response)){
				String password_entered = request.getParameter("password");
				if ((password_entered==null) || (password_entered.length()<8) || (password_entered.length()>16)) {
					response.sendRedirect("userinfo.jsp?code=nok");
				}
				else {
					 ReturnValues retVal = UserDetailsManager.changePassword(
							request.getSession().getAttribute("userName").toString(),
							password_entered,
							Integer.parseInt(request.getSession().getAttribute("usernumber").toString()));
					switch(retVal){
						case SUCCESS:
							response.sendRedirect("userinfo.jsp?code=ok");
						break;
						default:
							response.sendRedirect("userinfo.jsp?code=nok");
					}
				}
			}
		} catch (SQLException e){
			PageUtils.serverError(e,response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
