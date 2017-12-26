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
	private String device_name;
	private int status;
	
	private Equipment(){
		
	}
	
	private Equipment(String device_sn, String device_name, int status){
		this.device_sn = device_sn;
		this.device_name = device_name;
		this.status = status;
	}
	
	public String getDevice_sn() {
		return device_sn;
	}
	public void setDevice_sn(String device_sn) {
		this.device_sn = device_sn;
	}
	public String getDevice_name() {
		return device_name;
	}
	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
