

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import managers.GroupsManager;

import utilities.PageUtils;
import utilities.SessionUtils;

import structures.ReturnValues;

/**
 * Servlet implementation class addGroup
 */
public class addGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public addGroup() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			//Check live session
			if (SessionUtils.checkLiveSession(request, response)){
				String groupName = request.getParameter("group");
				
				if ((groupName.length()<1) || (groupName.length()>25)) {
					response.sendRedirect("addGroup.jsp?code=group");
					return;
				}
				ReturnValues retVal = GroupsManager.addGroup(groupName);
				switch (retVal) {
				case SUCCESS:
					response.sendRedirect("main.jsp?code=groupok");
					break;
				default:
					response.sendRedirect("addGroup.jsp?code=groupExists");
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
