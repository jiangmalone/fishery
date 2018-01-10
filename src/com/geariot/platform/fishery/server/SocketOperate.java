package com.geariot.platform.fishery.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketOperate extends Thread{

	private Socket socket;  
    
    public SocketOperate(Socket socket) {  
       this.socket=socket;  
    }  
    
    public void run(){
    	try{
    		//根据输入输出流和客户端连接
            InputStream inputStream=socket.getInputStream();//得到一个输入流，接收客户端传递的信息
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);//提高效率，将自己字节流转为字符流
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);//加入缓冲区
            while(true){
            	String str = bufferedReader.readLine();
            	System.out.println(str);
            }
            	
            
    	}catch (IOException e) {
    		e.printStackTrace();
    	}
    }
}
