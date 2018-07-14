package com.geariot.platform.fishery.dao;

import com.geariot.platform.fishery.entities.Dev_Trigger;

import java.util.List;

public interface Dev_TriggerDao {
    void save(Dev_Trigger trigger);

    int delete(int triggerid);

    int delete(String deviceSns);

    Dev_Trigger findTriggerBytriggerId(int triggerId);

    List<Dev_Trigger> findDev_TriggerBydevsn(String deviceSns);
    
    Dev_Trigger findDev_TriggerByDevsnAndWay(String deviceSns,int way);
}
