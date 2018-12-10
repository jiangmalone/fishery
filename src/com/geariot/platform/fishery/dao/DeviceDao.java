/**
 *
 */
package com.geariot.platform.fishery.dao;

import java.util.List;

import com.geariot.platform.fishery.entities.Fish_Category;
import com.geariot.platform.fishery.entities.Device;
import com.geariot.platform.fishery.model.Equipment;

/**
 * @author plong
 *
 */
public interface DeviceDao {

    void save(Device device);
    int delete(String devicesn);
    Device findDevice(String deviceSns);
    List<Device> getAllDevices(); 
    void updateIsOnline(Device device);
    List<Device> queryList(String device_sn,int page, int number);    
    List<Device> findDevicesByCompanyRelation(String relation, int page, int number);
    List<Device> findDevicesByCompanyRelation(String relation);
    List<Device> findByType(int type);
}
