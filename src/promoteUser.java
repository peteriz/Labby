

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import managers.UserDetailsManager;

import utilities.PageUtils;
import utilities.ServerProperties;
import structures.ReturnValues;

/**
 * Servlet implementation class promoteUser
 */
public class promoteUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public promoteUser() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
				ServerProperties sp = new ServerProperties();
				String passwordEntered = request.getParameter("password");
				String username =  request.getParameter("users");
				String tmp =  request.getParameter("usernumber");
				if (tmp == null){
					response.sendRedirect("index.jsp");
					return;
				}
				Integer usernumber = Integer.valueOf(tmp);
				String realPassword = sp.getMasterPassword();
				request.getSession().removeAttribute("admin");
				if ((passwordEntered == null)||(username == null)) {
					response.sendRedirect("index.jsp");
				}
				else if (passwordEntered.equals(realPassword)) {
					ReturnValues retVal = UserDetailsManager.promoteUser(username,usernumber);
					if (retVal == null){
						response.sendRedirect("index.jsp");
					}
					else if (retVal.equals(ReturnValues.SUCCESS)){
						response.sendRedirect("infoPage.jsp?code=j&page=index.jsp");
					}
					else {
						response.sendRedirect("index.jsp");
					}
				}
				else {
					response.sendRedirect("index.jsp");
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
