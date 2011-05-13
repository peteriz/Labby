

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import managers.GroupsManager;
import managers.UserDetailsManager;

import structures.UserDetails;
import structures.ReturnValues;
import utilities.PageUtils;
import utilities.SessionUtils;

/**
 * Servlet implementation class EditUsers
 */
public class EditUsers extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EditUsers() {
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
			if (utilities.SessionUtils.checkLiveSession(request, response)){
				String function = request.getParameter("function");
				String username = request.getParameter("username");
				String tmp = request.getParameter("usernumber");
				if ((function == null) || (username==null) || (tmp==null)){
					response.sendError(500);
					return;
				}
				Integer usernumber = Integer.parseInt(tmp);
				if ((username.length()<6) || (username.length()>10) || (usernumber<0)){
					response.sendError(500);
					return;
				}
				ReturnValues retVal = null;
				if (function.equals("select")){
					UserDetails user = UserDetailsManager.getUser(usernumber);
					if (user != null){
						PrintWriter out = response.getWriter();
						out.write(user.toXML());
						retVal = ReturnValues.SUCCESS;
					}
				}
				else if (function.equals("update")){
					if (request.getParameter("fullname") != null ) {
						String fullname = request.getParameter("fullname");
						retVal = UserDetailsManager.changeName(username, usernumber, fullname);
					}
					else if (request.getParameter("phonenum") != null ) {
						String phonenum = request.getParameter("phonenum");
						if (phonenum.matches(UserDetailsManager.PHONE_NUMBER_REGEX)){
							retVal = UserDetailsManager.changePhoneNumber(username, usernumber, phonenum);
						}
					}
					else if (request.getParameter("email") != null) {
						String email = request.getParameter("email");
						if (email.matches(UserDetailsManager.EMAIL_REGEX)){
							retVal = UserDetailsManager.changeAddress(username, usernumber, email);
						}
					}
					else if (request.getParameter("active") != null) {
						String active = request.getParameter("active");
						if (active.equals("Yes")) {
							retVal = UserDetailsManager.changeActive(username, usernumber, 1);
						}
						else if (active.equals("No")) {
							retVal = UserDetailsManager.changeActive(username, usernumber, 0);
						}
					}
					else if (request.getParameter("group") != null) {
						String group = request.getParameter("group");
						if (GroupsManager.isGroup(group)){
							retVal = UserDetailsManager.changeGroup(username, usernumber, group);
						}
					}
				}
				else if (function.equals("addPermission")){
					if (request.getParameter("per")!= null ){
						int per = Integer.parseInt(request.getParameter("per"));
						retVal = UserDetailsManager.addPermission(username, usernumber, per);
					}
				}
				else if (function.equals("delPermission")){
					if (request.getParameter("per")!= null ){
						int per = Integer.parseInt(request.getParameter("per"));
						retVal = UserDetailsManager.removePermission(username, usernumber, per);
					}
				}
				else if (function.equals("delete")){
					retVal = UserDetailsManager.deleteUser(username,usernumber);
					if ((retVal != null) && retVal.equals(ReturnValues.SUCCESS)){
						SessionUtils.removeUser(usernumber);
					}
				}
				if (retVal == null){
					response.sendError(500);
				}
				else if (retVal.equals(ReturnValues.FAILURE)){
					response.sendError(500);
				}
			}
		} catch (SQLException e){
			PageUtils.serverError(e,response);
		}
	}
}
