/**
 * 
 */
package com.geariot.platform.fishery.model;

import java.util.Date;

/**
 * @author mxy940127
 *
 */
public class Oxygen {
	
	private float oxygen;
	private String receiveTime;
	public Oxygen(){
		
	}
	public Oxygen(float oxygen, String receiveTime){
		this.oxygen = oxygen;
		this.receiveTime = receiveTime;
	}
	public float getOxygen() {
		return oxygen;
	}
	public void setOxygen(float oxygen) {
		this.oxygen = oxygen;
	}
	public String getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}
}
