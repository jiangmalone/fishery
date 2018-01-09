package com.geariot.platform.fishery.socket;
import java.io.ByteArrayOutputStream;
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
		SocketChannel readChannel = (SocketChannel) key.channel();
		// I/O读数据操作
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int len = 0;
		while (true) {
			buffer.clear();
			len = readChannel.read(buffer);
			if (len == -1)
				break;
			buffer.flip();
			while (buffer.hasRemaining()) {
				baos.write(buffer.get());
			}

			//System.out.println("服务器端接收到的数据：" + new String(baos.toByteArray()));
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("baos", baos);
			map.put("readChannel", readChannel);
			key.attach(map);
           new DataHandle().handle(key);
		}
	}
}