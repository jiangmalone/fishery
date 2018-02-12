package com.geariot.platform.fishery.socket;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
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
		SocketChannel readChannel = (SocketChannel) key.channel();
		// 创建读取的缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(100);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int len = 0;
		while (true) {
			buffer.clear();
			try {
				len = readChannel.read(buffer);

			} catch (IOException e) {
				log.debug("read时候IO异常");
			}
			if (len == -1)
				break;// 说明终端没有断开连接就关闭了
			if (len == 0)
				break;
			buffer.flip();
			while (buffer.hasRemaining()) {
				baos.write(buffer.get());

			}
		}
		byte[] data = baos.toByteArray();
		handle.handle(data, readChannel);
		NIOServer.addQueen(key);
	}
}
