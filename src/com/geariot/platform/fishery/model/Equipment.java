/**
 * 
 */
package com.geariot.platform.fishery.model;

/**
 * @author mxy940127
 *
 */
public class Equipment {
	private String device_sn;
	private String name;
	private int status;
	
	public Equipment(){
		
	}
	
	public Equipment(String device_sn, String name, int status){
		this.device_sn = device_sn;
		this.name = name;
		this.status = status;
	}
	
	public String getDevice_sn() {
		return device_sn;
	}
	public void setDevice_sn(String device_sn) {
		this.device_sn = device_sn;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
