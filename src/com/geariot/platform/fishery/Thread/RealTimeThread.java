package com.geariot.platform.fishery.Thread;

import java.io.IOException;

import javax.websocket.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.geariot.platform.fishery.entities.Sensor;
import com.geariot.platform.fishery.service.EquipmentService;

import net.sf.json.JSONObject;

public class RealTimeThread extends Thread {
	
	private static Logger logger = LogManager.getLogger(EquipmentService.class);
	private Session session;
    
	private String device_sn;
	
	private EquipmentService equipmentService;
	

    public RealTimeThread() {
		super();
	}

	public RealTimeThread(Session session,String device_sn) {
	
        this.session = session;
        this.device_sn = device_sn;
    }

    @Override
    public void run() {   	
    		this.equipmentService= BeanContext.getApplicationContext().getBean(EquipmentService.class);  
    		Sensor sensor = equipmentService.realTimeData(device_sn); 
    		
    		if(sensor!=null) {    	
    			while (true) {        			
        			sensor = equipmentService.realTimeData(device_sn); 
       
                	logger.debug("Webscoket返回数据：+sensor:"+sensor.toString());
                	/*JSONObject jo = JSONObject.fromObject(sensor);
                	session.getAsyncRemote().sendObject(jo);*/
                	JSONObject obj = JSONObject.fromObject(sensor);
                	session.getAsyncRemote().sendObject(sensor);
/*                	JSONObject obj = JSONObject.fromObject(sensor);
                    session.getAsyncRemote().sendText(obj.toString());*/
//                  session.getBasicRemote().sendObject(list.get(i)); //No encoder specified for object of class [class AlarmMessage]
        			/* session.getAsyncRemote().sendObject(i);*/
                    try {
                        //一分钟刷新一次
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                 
                }
    		}
    }
    		

        


}
