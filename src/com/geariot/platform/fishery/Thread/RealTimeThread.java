package com.geariot.platform.fishery.Thread;

import com.geariot.platform.fishery.controller.WebScoketController;
import com.geariot.platform.fishery.service.EquipmentService;
import com.geariot.platform.fishery.service.UserService;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.websocket.Session;
import java.util.Map;

public class RealTimeThread extends Thread {

    private static Logger logger = LogManager.getLogger(EquipmentService.class);
    private Session session;

    private String relation;

    private UserService userService;


    public RealTimeThread() {
        super();
    }

    public RealTimeThread(Session session, String relation) {

        this.session = session;
        this.relation = relation;
    }

    @Override
    public void run() {
        logger.debug("当前WebSocket的session：" + this.session);
        logger.debug("当前WebSocket的relation：" + this.relation);
        logger.debug("开启首页自动推送：--------------------------------");

//        session.getAsyncRemote().sendObject("111111");
//        WebScoketController.sendText(this.session,"22222222");


        this.userService = BeanContext.getApplicationContext().getBean(UserService.class);
        Map<String, Object> map = userService.HomePageDetail(relation);

//        if (!((ArrayList) map.get("myHome")).isEmpty()) {
        if (map != null) {
            while (true) {
                map = userService.HomePageDetail(relation);
                JSONObject obj = JSONObject.fromObject(map);
                logger.debug("Webscoket返回数据：+map:" + obj);
                WebScoketController.sendText(this.session,obj.toString());
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
