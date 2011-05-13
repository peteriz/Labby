

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import utilities.ServerProperties;

/**
 * Servlet implementation class masterLogin
 */
public class masterLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public masterLogin() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ServerProperties sp = new ServerProperties();
		String passwordEntered = request.getParameter("password");
		String realPassword = sp.getMasterPassword();
		if (passwordEntered == null) {
			response.sendRedirect("index.jsp");
		}
		else if (passwordEntered.equals(realPassword)) {
			HttpSession ses = request.getSession();
			ses.setAttribute("admin", "ok");
			response.sendRedirect("newAdmin.jsp");
		}
		else {
			response.sendRedirect("index.jsp");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
