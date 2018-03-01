package com.geariot.platform.fishery.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NIOServer {
	private static final Logger log = LogManager.getLogger(NIOServer.class);
	
	  //存储SelectionKey的队列
    private static List<SelectionKey> Queen = new ArrayList<SelectionKey>();
    private static Selector selector = null;

    //添加SelectionKey到队列
    public static void addQueen(SelectionKey key){
        synchronized (Queen) {
        	Queen.add(key);
            //唤醒主线程
            selector.wakeup();
        }
    }

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
		selector = Selector.open();
		// 将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件,注册该事件后，
		// 当该事件到达时，selector.select()会返回，如果该事件没到达selector.select()会一直阻塞。
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
	}

	/**
	 * 采用轮询的方式监听selector上是否有需要处理的事件，如果有，则进行处理
	 * 
	 * @throws IOException
	 */
	public void listen() throws IOException {
		
		log.debug("服务端启动成功！");
		// 轮询访问selector
		RequestProcessor requestProcessor=new RequestProcessor();
		while (true) {
			// 获取可用I/O通道,获得有多少可用的通道
			System.out.println("正在监听------------");
			int num=selector.select();
			 // 判断是否存在可用的通道
				if(num>0) {
			// 获得selector中选中的项的迭代器，选中的项为注册的事件
				Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
				
				while (ite.hasNext()) {
					SelectionKey key = (SelectionKey) ite.next();
					// 客户端请求连接事件
					try {
						if (key.isAcceptable()) {
							
							ServerSocketChannel server = (ServerSocketChannel) key.channel();
							// 获得和客户端连接的通道
							SocketChannel channel= server.accept();
								if(channel==null) {
									continue;
								}
								String ip = channel.socket().getInetAddress().getHostAddress();
						 		log.debug("# " + ip + " connect to server!!!");
								// 设置成非阻塞，所以设置非阻塞也没用
								channel.configureBlocking(false);
								// 在这里可以给客户端发送信息哦
								// channel.write(ByteBuffer.wrap(new String("send client one
								// message").getBytes()));
								// 在和客户端连接成功之后，为了可以接收到客户端的信息，需要给通道设置读的权限。
								channel.register(selector, SelectionKey.OP_READ);
							
							// 获得了可读的事件
						} else if (key.isReadable()) {
							// 取消读事件的监控 并在后面重新注册读，不然下一次读会阻塞
							key.cancel();
							// 调用读操作
							requestProcessor.ProcessorRequest(key);
						}
						// 删除已选的key,以防重复处理
						ite.remove();
					} catch (CancelledKeyException e) {
						log.debug("cancelledKeyException");
					}
					
					
				}
			}else{
				//因为前面取消了key的监听，下一次读会阻塞，所以一旦num=0就会跳转到这里重新注册读事件
                synchronized (Queen) {
                    while(Queen.size() > 0){
                        SelectionKey key = Queen.remove(0);
                        //注册读事件
                        SocketChannel channel = (SocketChannel) key.channel();
                       
                        channel.register(selector, SelectionKey.OP_READ);
                    }
                }
            }
		}
		
	}
}