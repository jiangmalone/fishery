package com.geariot.platform.fishery.Thread;

import java.io.IOException;
import java.util.Map;

import javax.websocket.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.geariot.platform.fishery.entities.Sensor;
import com.geariot.platform.fishery.service.EquipmentService;
import com.geariot.platform.fishery.service.UserService;

import net.sf.json.JSONObject;

public class RealTimeThread extends Thread {
	
	private static Logger logger = LogManager.getLogger(EquipmentService.class);
	private Session session;
    
	private String relation;
	
	private UserService userService;
	

    public RealTimeThread() {
		super();
	}

	public RealTimeThread(Session session,String relation) {
	
        this.session = session;
        this.relation = relation;
    }

    @Override
    public void run() {   	
    		this.userService= BeanContext.getApplicationContext().getBean(UserService.class);  
    		Map<String, Object> map = userService.HomePageDetail(relation);
    		
    		if(map!=null) {    	
    			while (true) {        			
//        			sensor = equipmentService.realTimeData(relation); 
       
//                	logger.debug("Webscoket返回数据：+sensor:"+sensor.toString());
    				logger.debug("Webscoket返回数据：+map:"+map.toString());
                	/*JSONObject jo = JSONObject.fromObject(sensor);
                	session.getAsyncRemote().sendObject(jo);*/
//                	JSONObject obj = JSONObject.fromObject(sensor);
                	session.getAsyncRemote().sendObject(map);
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
