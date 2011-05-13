

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import managers.UserDetailsManager;


import structures.UserDetails;
import utilities.PageUtils;
import utilities.SessionUtils;

/**
 * Servlet implementation class systemLogin
 */
public class systemLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public systemLogin() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			if (!SessionUtils.checkRedirect(request, response)){
				
				String username = request.getParameter("username");
				String password = request.getParameter("password");
				if ((username.length()<6) || (username.length()>10) ||
						(password.length()<8) || (password.length()>16)) {
					response.sendRedirect("login.jsp?code=a");
				}
				else {
					Integer usernumber = UserDetailsManager.getUserNumber(username, password);
					if (username == null){
						response.sendError(500, "Database is not worxing");
					}
					else {
						UserDetails user = UserDetailsManager.getUser(usernumber);
						if (user==null){
							response.sendRedirect("login.jsp?code=a");
						}
						else if (!user.isActive()){
							response.sendRedirect("login.jsp?code=b");
						}
						else {
							HttpSession session = request.getSession();
							session.setAttribute("userName", user.getUsername());
							session.setAttribute("userRole", user.isManager()?"manager":"regular");
							session.setAttribute("usernumber", user.getUsernumber());
							session.setAttribute("fullName", user.getFullname());
							SessionUtils.addSession(session);
							response.sendRedirect("main.jsp");
						}
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
