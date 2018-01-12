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
    //构造线程池 这边线程池应该放到外面，一个单例
    private  ExecutorService  executorService  = Executors.newFixedThreadPool(10);
   
   
	public  void ProcessorRequest( SelectionKey key){
		try {
			
        	read(key);
        	System.out.println("read wan");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        //获得线程并执行
        /*executorService.submit(new Runnable() {

            @Override
            public void run(){
                try {
					
                	read(key);
                	System.out.println("read wan");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}    
            }
        });*/
    }

	public void read(SelectionKey key) throws IOException {
		// 服务器可读取消息:得到事件发生的Socket通道
				SocketChannel readChannel = (SocketChannel) key.channel();
				// 创建读取的缓冲区
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				try
				{
					readChannel.read(buffer);
				}
				catch (IOException e1)
				{
					System.out.println("Connection reset by peer 3");
					
				}
				byte[] data = buffer.array();
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("data", data);
			map.put("readChannel", readChannel);
			key.attach(map);
           new DataHandle().handle(key); 
           //如果不关闭，这边都不执行
           System.out.println("handle代码处理完");
          
		}
	}
