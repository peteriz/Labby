

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utilities.PageUtils;

import managers.UserDetailsManager;

/**
 * Servlet implementation class ResendPassword
 */
public class ResendPassword extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ResendPassword() {
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
			String username = request.getParameter("username");
			String email = request.getParameter("email");
			if ((username == null) || (email == null)){
				response.sendRedirect("forgot.jsp?code=c");
			}
			else if ((username.length() < 6) || (username.length() > 10)){
				response.sendRedirect("forgot.jsp?code=c");
			}
			else if (!email.matches(UserDetailsManager.EMAIL_REGEX)){
				response.sendRedirect("forgot.jsp?code=c");
			}
			else if (UserDetailsManager.retrievePassword(username, email)){
				response.sendRedirect("infoPage.jsp?page=index.jsp&code=rtrvd");
			}
			else {
				response.sendRedirect("forgot.jsp?code=d");
		}	
		} catch (SQLException e){
			PageUtils.serverError(e,response);
		}
	}

}
