

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import utilities.ServerProperties;

/**
 * Application Lifecycle Listener implementation class InitializeLabbyParams
 *
 */
public class InitializeLabbyParams implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public InitializeLabbyParams() {
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
    	/* read servlet parameters  from web.xml */
        String dbusername = arg0.getServletContext().getInitParameter("dbusername");
        String dbpassword = arg0.getServletContext().getInitParameter("dbpassword");
        int dbport = Integer.parseInt(arg0.getServletContext().getInitParameter("dbport"));
        String dbname = arg0.getServletContext().getInitParameter("dbname");
        String masterpassword = arg0.getServletContext().getInitParameter("masterpassword");
        int maxconnections = Integer.parseInt(arg0.getServletContext().getInitParameter("maxconnections"));
        String emailusername = arg0.getServletContext().getInitParameter("emailusername");
        String emailpassword = arg0.getServletContext().getInitParameter("emailpassword");
        
        /* initialize server properties static class */
        ServerProperties sp = ServerProperties.getInstance();
        sp.setDBusername(dbusername);
        sp.setDBpassword(dbpassword);
        sp.setDBport(dbport);
        sp.setDatabase(dbname);
        sp.setMasterPassword(masterpassword);
        sp.setMaximumDBConnections(maxconnections);
        sp.setEmailUserName(emailusername);
        sp.setEmailPassword(emailpassword);
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
    }
	
}
