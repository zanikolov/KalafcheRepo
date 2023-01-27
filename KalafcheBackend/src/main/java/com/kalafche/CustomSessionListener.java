package com.kalafche;

import java.util.Date;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class CustomSessionListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		System.out.println(">>>>>>>>>>>> Session created: " + new Date());
		
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		System.out.println(">>>>>>>>>>>> Session destroyed: " + new Date());		
	}

}
