package com.geariot.platform.fishery.controller;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geariot.platform.fishery.Thread.RealTimeThread;
import com.geariot.platform.fishery.Thread.ServerEncoder;

@ServerEndpoint(value ="/websocket",encoders= {ServerEncoder.class})
public class WebScoketController {
	private Logger logger = LogManager.getLogger(WebScoketController.class);
	//静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
		private static int onlineCount = 0;

		//concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
		private static CopyOnWriteArraySet<WebScoketController> webSocketSet = new CopyOnWriteArraySet<WebScoketController>();

		//与某个客户端的连接会话，需要通过它来给客户端发送数据
		private Session session;
		
		RealTimeThread rtthread;

		/**
		 * 连接建立成功调用的方法
		 * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
		 */
		@OnOpen
		public void onOpen(Session session){
			this.session = session;
			webSocketSet.add(this);     //加入set中
			addOnlineCount();           //在线数加1
			logger.debug("有新连接加入！当前在线人数为" + getOnlineCount());
		}

		/**
		 * 连接关闭调用的方法
		 */
		@OnClose
		public void onClose(){
			
			webSocketSet.remove(this);  //从set中删除
			subOnlineCount();           //在线数减1
			if(rtthread!=null) {
				rtthread.stop();
			}
			logger.debug("有一连接关闭！当前在线人数为" + getOnlineCount());
		}

		/**
		 * 收到客户端消息后调用的方法
		 * @param message 客户端发送过来的消息
		 * @param session 可选的参数
		 */
		@OnMessage
		public void onMessage(String message, Session session) {
			logger.debug("启用Webscoket");
			logger.debug("来自客户端的消息:" + message);
			rtthread = new RealTimeThread(session, message);
			rtthread.start();
	  /*      session.getAsyncRemote().*/
		}

		/**
		 * 发生错误时调用
		 * @param session
		 * @param error
		 */
		@OnError
		public void onError(Session session, Throwable error){
			logger.debug("Webscoket发生错误");
			error.printStackTrace();
		}

		/**
		 * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
		 * @param message
		 * @throws IOException
		 */
		public void sendMessage(String message) throws IOException{
			//this.session.getBasicRemote().sendText(message);
			//this.session.getAsyncRemote().sendText(message);
		}

		public static synchronized int getOnlineCount() {
			return onlineCount;
		}

		public static synchronized void addOnlineCount() {
			WebScoketController.onlineCount++;
		}

		public static synchronized void subOnlineCount() {
			WebScoketController.onlineCount--;
		}
}
