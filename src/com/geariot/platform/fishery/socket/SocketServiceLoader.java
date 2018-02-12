package com.geariot.platform.fishery.socket;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class SocketServiceLoader implements ServletContextListener{ 
	// socket server 线程
	//1
	private static final Logger log = LogManager.getLogger(SocketServiceLoader.class);
	

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {

		final NIOServer server = new NIOServer();
		    new Thread(new Runnable() {

			@Override
			public void run() {
				
				try {
					server.initServer(5678);
					server.listen();
				} catch (IOException e) {
					
					log.debug("nio服务器启动失败");
				}

			}

		}).start();

	}
}