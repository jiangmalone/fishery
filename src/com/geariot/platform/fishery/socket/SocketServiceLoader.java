package com.geariot.platform.fishery.socket;

import java.io.IOException;

import javax.servlet.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class SocketServiceLoader implements ServletContextListener{ 
	// socket server 线程
	//1
	private static final Logger log = LogManager.getLogger(SocketServiceLoader.class);
	//private SocketThread socketThread;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		/*if (null != socketThread && !socketThread.isInterrupted()) {
			socketThread.closeSocketServer();
			socketThread.interrupt();
		}*/
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		NIOServer server = new NIOServer();

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					server.initServer(5678);
					server.listen();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					log.debug("nio服务器启动失败");
				}

			}

		}).start();

	}
}