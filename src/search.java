

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import structures.Instrument;
import utilities.PageUtils;

import managers.InstrumentManager;

/**
 * Servlet implementation class search
 */
public class search extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public search() {
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
				String searchQuery = request.getParameter("query");
				response.addHeader("toShow", request.getParameter("toShow"));
				if (searchQuery == null){
					response.sendError(500);
					return;
				}
				String username = request.getSession().getAttribute("userName").toString();
				Integer usernumber = Integer.valueOf(request.getSession().getAttribute("usernumber").toString());
				LinkedList<Instrument> retVal = InstrumentManager.searchInstruments(usernumber, username, searchQuery);
	
				if (retVal == null){
					response.sendError(500);
				}
				else {
					PrintWriter out = response.getWriter();
					if (retVal.isEmpty())  {
						out.write("<p class=\"grey\">No search results</p>");
					}
					else {
						out.write("<table class=\"tablesorter\">\n");
						out.write("<thead>\n");
						out.write("<tr>\n");
						out.write("\t<th width=\"10%\">ID</th>\n");
						out.write("\t<th>Type</th>\n");
						out.write("\t<th>Description</th>\n");
						out.write("</tr>");
						out.write("</thead>\n");
						out.write("<tbody>\n");
						for (Instrument instrument: retVal) {
							out.write("<tr>\n");
							out.write("\t<td>"+instrument.getInstrumentid()+"</td>\n");
							out.write("\t<td>"+instrument.getType()+"</td>\n");
							out.write("\t<td>"+instrument.getDescription()+"</td>\n");
							out.write("</tr>\n");
						}
						out.write("</tbody>\n");
						out.write("</table>\n");
					}
				}
			}
		} catch (SQLException e){
			PageUtils.serverError(e,response);
		}
	}
}
