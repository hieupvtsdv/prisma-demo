package com.journaldev;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JournalDevServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		Logger logger = LogManager.getLogger(JournalDevServlet.class);
		for(int i = 0 ; i < 1000 ; i++){
			logger.trace("Index :: "+i+" :: JournalDev Database Logging Message !");
			LogManager.getRootLogger().error("Index :: "+i+" :: JournalDev Database Logging Message !");
		}
	}
}
