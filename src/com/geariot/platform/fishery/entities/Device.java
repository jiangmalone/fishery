package com.geariot.platform.fishery.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="javaClassName")
public class Device {

    private int id;							//设备Id,自增
    private String device_sn;				//设备编号
    private int type;                      //设备类型  1 传感器 2 一体机 3 控制器
    private boolean isOnline;				//设备是否在线

   
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getDevice_sn() {
        return device_sn;
    }
    public void setDevice_sn(String device_sn) {
        this.device_sn = device_sn;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public boolean isOnline() {
		return isOnline;
	}
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	} 
}
