package com.geariot.platform.fishery.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class SocketServerLoader implements ServletContextListener{

	private SocketThread socketThread; 
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		if(null!=socketThread && !socketThread.isInterrupted())  
        {  
         socketThread.shutdown(); 
         socketThread.interrupt();  
        }  
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		if(null==socketThread)  
        {  
         //新建线程类  
         socketThread=new SocketThread();  
         //启动线程  
         socketThread.start();  
        }  
		
	}

}
