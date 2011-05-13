

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import managers.UserDetailsManager;

import org.apache.tomcat.util.http.fileupload.DiskFileUpload;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;

import utilities.PageUtils;
import utilities.SessionUtils;
import structures.ReturnValues;

/**
 * Servlet implementation class createUser
 */
public class createUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public createUser() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if (!SessionUtils.checkRedirect(request, response)){
			DiskFileUpload fu = new  DiskFileUpload();
			fu.setSizeMax(540000);
			HashMap<String, String> parameters = new HashMap<String, String>();
			try {
				List fileItems = fu.parseRequest(request);
				for (Object item : fileItems){
					FileItem fi = (FileItem)item;
					parameters.put(fi.getFieldName(), fi.getString());
				}
			} catch (FileUploadException e) {
				response.sendError(500,"Communication problems");
				return;
			}
			
			String username_entered = parameters.get("username");
			String password_entered = parameters.get("password");
			String password_verification = parameters.get("password2");
			String fullname  = parameters.get("fullname");
			String phonenum = parameters.get("phone");
			String user_email = parameters.get("email");
		
			if ((username_entered==null) || (username_entered.length()<6) ||
					(username_entered.length()>10)) {
				response.sendError(500, "wrong username length");
			}
			else if ((password_entered==null) || (password_verification==null) ||
					(!password_entered.equals(password_verification)) ||
					(password_entered.length()<8) ||(password_entered.length()>16)) {
				response.sendError(500,"wrong password length or passwords aren't equal");
			}
			else if ((fullname==null) || (fullname.length()<1) || (fullname.length()>50)) {
				response.sendError(500,"wrong fullname length");
			}
			else if ((phonenum==null) || !phonenum.matches(UserDetailsManager.PHONE_NUMBER_REGEX)) {
				response.sendError(500,"phonenum contains non numerical characters");
			}
			else if ((user_email!=null) && !user_email.matches(UserDetailsManager.EMAIL_REGEX)){
				response.sendError(500,"wrong email format");
			} 
			else{
				try{
					ReturnValues retVal = UserDetailsManager.addUser(username_entered,password_entered,fullname,phonenum,user_email);
					if (retVal == null){
						response.sendError(500,"Server error");
					}
					else if (retVal.equals(ReturnValues.SUCCESS)){
						response.sendRedirect("infoPage.jsp?code=e&page=index.jsp");
					}
					else {
						response.sendRedirect("newUser.jsp?code=d&fullname=" + fullname.replaceAll(" ", "+") + "&phonenum=" + 
								phonenum + "&email=" + user_email);
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
