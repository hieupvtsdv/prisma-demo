package com.journaldev;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;

public class JournalDevServletContextListener implements ServletContextListener{
	
	private InitialContext context = null;

	public void contextDestroyed(ServletContextEvent event) {

	}

	public void contextInitialized(ServletContextEvent event) {
		try {
			// Get initial context
			context = new InitialContext();
			// Get a reference for sub context env
			Context envContext = (Context)context.lookup("java:comp/env");
			// Get a reference for sub context jdbc and then locating the data source defined
			LogManager.getRootLogger().error(((Context)envContext.lookup("jdbc")).lookup("JournalDevDB"));
			
		} catch (NamingException e) {
			LogManager.getRootLogger().error(e);
		}	
	}

}
