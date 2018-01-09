package com.geariot.platform.fishery.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class NIOServer {
//1
	private static final Logger log = LogManager.getLogger(NIOServer.class);
	// 通道管理器
	private static Selector selector;
	/*// 存储SelectionKey的队列
	private static List<SelectionKey> writeQueen = new ArrayList<SelectionKey>();
	
	// 添加SelectionKey到队列
	public static void addWriteQueen(SelectionKey key) {
		synchronized (writeQueen) {
			writeQueen.add(key);
			// 唤醒主线程
			selector.wakeup();
		}
	}*/

	/**
	 * 获得一个ServerSocket通道，并对该通道做一些初始化的工作
	 * 
	 * @param port
	 *            绑定的端口号
	 * @throws IOException
	 */
	

	public void initServer(int port) throws IOException {
		// 获得一个ServerSocket通道
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		// 设置通道为非阻塞
		serverChannel.configureBlocking(false);
		// 将该通道对应的ServerSocket绑定到port端口
		serverChannel.socket().bind(new InetSocketAddress(port));
		// 获得一个通道管理器
		this.selector = Selector.open();
		// 将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件,注册该事件后，
		// 当该事件到达时，selector.select()会返回，如果该事件没到达selector.select()会一直阻塞。
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
	}

	/**
		 * 采用轮询的方式监听selector上是否有需要处理的事件，如果有，则进行处理
		 * 
		 * @throws IOException
		 */
		public void listen(){
			log.debug("服务端启动成功！");
		// 轮询访问selector
		while (true) {
			// 获取可用I/O通道,获得有多少可用的通道
			int num=0;
			try {
				num = selector.select();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
				log.debug("Connection reset by peer 1 ");
			}
			if (num > 0) { // 判断是否存在可用的通道
				// 获得selector中选中的项的迭代器，选中的项为注册的事件
				Iterator<SelectionKey> ite = this.selector.selectedKeys().iterator();
				while (ite.hasNext()) {
					SelectionKey key = (SelectionKey) ite.next();
					// 删除已选的key,以防重复处理
					ite.remove();
					// 客户端请求连接事件
					try {
						if (key.isAcceptable()) {
							ServerSocketChannel server = (ServerSocketChannel) key.channel();
							// 获得和客户端连接的通道
							SocketChannel channel;
							try {
								channel = server.accept();
								String ip = channel.socket().getInetAddress().getHostAddress();
								log.debug("# " + ip + " connect to server!!!");
								// 设置成非阻塞
								channel.configureBlocking(false);
								// 在这里可以给客户端发送信息哦
								// channel.write(ByteBuffer.wrap(new String("send client one
								// message").getBytes()));
								// 在和客户端连接成功之后，为了可以接收到客户端的信息，需要给通道设置读的权限。
								channel.register(this.selector, SelectionKey.OP_READ);
							} catch (IOException e) {
								log.debug("Connection reset by peer 2");
							}
							// 获得了可读的事件
						} else if (key.isReadable()) {
	                        //取消读事件的监控
	                        key.cancel();
	                        //调用读操作
	                        new RequestProcessor().ProcessorRequest(key);
	                    } /*else if (key.isWritable()) {
	                        //取消读事件的监控
	                        key.cancel();
	                        //调用写操作
	                        ResponeProcessor.ProcessorRespone(key);
	                    }*/
					}catch(CancelledKeyException e){ 
						log.debug("cancelledKeyException");
					}
				   
				}
				}
	          /*  else{
	                synchronized (writeQueen) {
	                    while(writeQueen.size() > 0){
	                        SelectionKey key = writeQueen.remove(0);
	                        //注册写事件
	                        SocketChannel channel = (SocketChannel) key.channel();
	                        Object attachment = key.attachment();
	                        try {
								channel.register(selector, SelectionKey.OP_WRITE,attachment);
							} catch (ClosedChannelException e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
								log.debug("ClosedChannelException");
							}
	                    }
	                }
	               
	            }*/
	        }
		}
}