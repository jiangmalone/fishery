/**
 * 
 */
package com.geariot.platform.fishery.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geariot.platform.fishery.socket.NIOServer;
import com.geariot.platform.fishery.utils.Constants;

/**
 * @author mxy940127
 *
 */
public class SocketThread extends Thread{

	private static final Logger log = LogManager.getLogger(SocketThread.class);
	
	private ServerSocket serverSocket;
	
	private boolean flag;
	
	private ExecutorService pool;
	
	public void run()
	{
		try {
			serverSocket = new ServerSocket(Constants.PORT);
			log.info("server socket begins to listen");
			this.pool = Executors.newFixedThreadPool(Constants.POOL_SIZE);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		flag = true;
        Socket socket = null;
        while(flag)
        {
        	try {
        		log.info("start received msg");
				socket = serverSocket.accept();
				pool.execute(new ProcessThread(socket));
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(serverSocket.isClosed())
				{
					flag = false;
				}
				else
				{
					e.printStackTrace();
				}
			}
        	
        }
	}
	
	public void shutdown()
	{
		try {
			serverSocket.close();
			flag = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 
}
