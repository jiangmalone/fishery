package com.geariot.platform.fishery.utils;

import cmcc.iot.onenet.javasdk.api.datastreams.GetDatastreamApi;
import cmcc.iot.onenet.javasdk.response.BasicResponse;
import cmcc.iot.onenet.javasdk.response.datastreams.DatastreamsResponse;

import com.geariot.platform.fishery.dao.LimitDao;
import com.geariot.platform.fishery.entities.Limit_Install;
import com.geariot.platform.fishery.timer.CMDUtils;
import com.geariot.platform.fishery.wxutils.WechatSendMessageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Timer;
import java.util.TimerTask;

public class HighlimitTrigger {

    private static final Logger log = LogManager.getLogger(HighlimitTrigger.class);
    private static String key = "7zMmzMWnY1jlegImd=m4p9EgZiI=";
    
    @Autowired
    private LimitDao limitDao;
    Timer timer;

    public HighlimitTrigger(int sec, final String device_sn, final String text, final String publicOpenID) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                //关闭增氧机
                log.debug("高限增氧结束，准备关闭增氧机");

                String id = text.substring(0, 3);
                int port = Integer.parseInt(id) - 1;
                GetDatastreamApi api = new GetDatastreamApi(device_sn, id, key);
                BasicResponse<DatastreamsResponse> response = api.executeApi();
                
                log.debug("高限增氧结束，上下限有效性恢复");
                Limit_Install limit = limitDao.findLimitByDeviceSnsAndWay(device_sn, port);
                limit.setValid(true);
                limitDao.updateLimit(limit);
                
                int currentvalue = Integer.parseInt(response.data.getCurrentValue().toString());

                if (currentvalue == 1) {//增氧机处于打开状态，进行关闭操作
                    int results = CMDUtils.sendStrCmd(device_sn, text);
                    if (results == 0) {
                        WechatSendMessageUtils.sendWechatOnOffMessages("高限增氧结束，关闭增氧机成功", publicOpenID, device_sn);
                    } else {//打开
                        WechatSendMessageUtils.sendWechatOnOffMessages("高限增氧结束，关闭增氧机失败", publicOpenID, device_sn);
                    }
                } else {
//					WechatSendMessageUtils.sendWechatOnOffMessages("高限增氧结束，增氧机已关闭，不进行操作", publicOpenID,device_sn);
                    log.debug("高限增氧结束，增氧机已关闭，不进行操作。操作人：" + publicOpenID + "；操作设备编号：" + device_sn);
                }

                timer.cancel();
            }
        }, sec * 1000);
    }
}
