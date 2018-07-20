package com.geariot.platform.fishery.Thread;

import java.io.IOException;

import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;

import com.geariot.platform.fishery.service.EquipmentService;

public class RealTimeThread implements Runnable {
	
	@Autowired
	private EquipmentService equipmentService;
	
	private Session session;
    
    private static int i;

    public RealTimeThread(Session session) {
        this.session = session;
    }

    @Override
    public void run() {
        while (true) {
        	
        	
            try {
                session.getBasicRemote().sendText("消息"+i);
//                            session.getBasicRemote().sendObject(list.get(i)); //No encoder specified for object of class [class AlarmMessage]
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                //一秒刷新一次
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
    }

}
