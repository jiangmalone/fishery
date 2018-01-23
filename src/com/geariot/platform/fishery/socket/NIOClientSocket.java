package com.geariot.platform.fishery.socket;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.geariot.platform.fishery.utils.CommonUtils;
public class NIOClientSocket {
	 public static void main(String[] args) throws IOException {
	        //使用线程模拟用户 并发访问
		while(true) {
	        for (int i = 0; i < 2; i++) {
	            new Thread(){
	                public void run() {
	                    try {
	                        //1.创建SocketChannel
	                        SocketChannel socketChannel=SocketChannel.open();
	                        //2.连接服务器
	                        socketChannel.connect(new InetSocketAddress("127.0.0.1",5678));

	                        //写数据
	                        byte[] msg=new byte[55];
	                        msg[0]=(byte) 0x5A;
	                        msg[1]=(byte) 0xA5;
						if (System.currentTimeMillis()%2==0) {
							msg[2] = (byte) 0x01;
						} else
							msg[2] = (byte) 0x03;
						if (System.currentTimeMillis()%2==0) {
							msg[5] = (byte) 0x01;
						} else
							msg[5] = (byte) 0x02;
	                        msg[3]=(byte) 0x00;msg[4]=(byte) 0x01;
	                        msg[6]=(byte) 0x00;msg[7]=(byte) 0x01;msg[8]=(byte) 0x00;msg[9]=(byte) 0x00;msg[10]=(byte) 0x00;
	                        msg[11]=(byte) 0x00;msg[12]=(byte) 0x00;msg[13]=(byte) 0x00;msg[14]=(byte) 0x00;msg[15]=(byte) 0x00;
	                        msg[16]=(byte) 0x00;msg[17]=(byte) 0x15;msg[18]=CommonUtils.arrayMerge(msg, 2, 16);msg[19]=(byte) 0x0D;msg[20]=(byte) 0x0A;
	                        msg[21]=(byte) 0x0A;msg[22]=(byte) 0x0D;
	                        ByteBuffer buffer=ByteBuffer.allocate(1024);
	                       buffer=ByteBuffer.wrap(msg);
	                        socketChannel.write(buffer);
	                        //socketChannel.shutdownOutput();

	                        //读数据
	                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	                        int len = 0;
	                        while (true) {
	                            buffer.clear();
	                            len = socketChannel.read(buffer);
	                            if (len == -1)
	                                break;
	                            buffer.flip();
	                            while (buffer.hasRemaining()) {
	                                bos.write(buffer.get());
	                            }
	                        }

	                        System.out.println("客户端收到:"+new String(bos.toByteArray()));

	                        socketChannel.close();
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
	                };
	            }.start();
	        }
	        for (int i = 0; i < 2; i++) {
	            new Thread(){
	                public void run() {
	                    try {
	                        //1.创建SocketChannel
	                        SocketChannel socketChannel=SocketChannel.open();
	                        //2.连接服务器
	                        socketChannel.connect(new InetSocketAddress("127.0.0.1",5678));

	                        //写数据
	                        byte[] msg=new byte[55];
	                        msg[0]=(byte) 0x5A;
	                        msg[1]=(byte) 0xA5;
						if (System.currentTimeMillis()%2==0) {
							msg[2] = (byte) 0x02;
						} else
							msg[2] = (byte) 0x03;
						if (System.currentTimeMillis()%2==0) {
							msg[5] = (byte) 0x02;
						} else
							msg[5] = (byte) 0x01;
	                        msg[3]=(byte) 0x00;msg[4]=(byte) 0x01;
	                        msg[6]=(byte) 0x00;msg[7]=(byte) 0x01;msg[8]=(byte) 0x00;msg[9]=(byte) 0x00;msg[10]=(byte) 0x00;
	                        msg[11]=(byte) 0x01;msg[12]=(byte) 0x00;msg[13]=(byte) 0x00;msg[14]=(byte) 0x00;msg[15]=(byte) 0x00;
	                        msg[16]=(byte) 0x00;msg[17]=(byte) 0x15;msg[18]=CommonUtils.arrayMerge(msg, 2, 16);msg[19]=(byte) 0x0D;msg[20]=(byte) 0x0A;
	                        msg[21]=(byte) 0x0A;msg[22]=(byte) 0x0D;
	                        ByteBuffer buffer=ByteBuffer.allocate(1024);
	                       buffer=ByteBuffer.wrap(msg);
	                        socketChannel.write(buffer);
	                        //socketChannel.shutdownOutput();

						// 读数据
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						int len = 0;
						while (true) {
							buffer.clear();
							len = socketChannel.read(buffer);
							if (len == -1)
								break;
							buffer.flip();
							while (buffer.hasRemaining()) {
								bos.write(buffer.get());
							}
						}

						System.out.println("客户端收到:" + new String(bos.toByteArray()));

	                        socketChannel.close();
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
	                };
	            }.start();
	        }
	        try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	    }
}
