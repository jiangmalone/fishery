package com.geariot.platform.fishery.socket;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * 读操作
 */
public class RequestProcessor {
//1
	private static final Logger log = LogManager.getLogger(RequestProcessor.class);
    private  ExecutorService  executorService  = Executors.newFixedThreadPool(10);
    private DataHandle handle=new DataHandle();
   
	public void ProcessorRequest(final SelectionKey key){
		
        //获得线程并执行
        executorService.submit(new Runnable() {

            @Override
            public void run(){ 
                read(key);    
            }
        });
    } 

	public  void read(final SelectionKey key) {
		// 服务器可读取消息:得到事件发生的Socket通道
		log.debug("新消息来了，准备开始读，key为"+key.toString());		
		SocketChannel readChannel = (SocketChannel) key.channel();
		
				// 创建读取的缓冲区
				ByteBuffer buffer = ByteBuffer.allocate(100);
				
					try {
						readChannel.read(buffer);
					} catch (IOException e) {
						
					}
					
						
					
					byte[] data = buffer.array();
				
				handle.handle(data,readChannel); 
           //将下一个读放进队列里面，并在主线程里面注册下一次读
		if(data[0]!=0) {
				NIOServer.addQueen(key);
		}
           
		}
	}
