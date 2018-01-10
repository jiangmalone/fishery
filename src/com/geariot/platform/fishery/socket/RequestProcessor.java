package com.geariot.platform.fishery.socket;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 读操作
 */
public class RequestProcessor {
//1
    //构造线程池
    private  ExecutorService  executorService  = Executors.newFixedThreadPool(10);
   
   
	public  void ProcessorRequest( SelectionKey key){
        //获得线程并执行
        executorService.submit(new Runnable() {

            @Override
            public void run(){
                try {
					read(key);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}    
            }
        });
    }

	public  void read(SelectionKey key) throws IOException {
		// 服务器可读取消息:得到事件发生的Socket通道
				SocketChannel readChannel = (SocketChannel) key.channel();
				// 创建读取的缓冲区
				ByteBuffer buffer = ByteBuffer.allocate(100);
				try
				{
					readChannel.read(buffer);
				}
				catch (IOException e1)
				{
					System.out.println("Connection reset by peer 3");
					try
					{
						readChannel.close();
						System.out.println("Close channel");
					} 
					catch (IOException e)
					{
						System.out.println("Close channel Exception");
					}
				}
				byte[] data = buffer.array();
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("data", data);
			map.put("readChannel", readChannel);
			key.attach(map);
           new DataHandle().handle(key); 
		}
	}
