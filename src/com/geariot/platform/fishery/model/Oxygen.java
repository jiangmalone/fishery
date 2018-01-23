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
	private Date receiveTime;
	public Oxygen(){
		
	}
	public Oxygen(float oxygen, Date receiveTime){
		this.oxygen = oxygen;
		this.receiveTime = receiveTime;
	}
	public float getOxygen() {
		return oxygen;
	}
	public void setOxygen(float oxygen) {
		this.oxygen = oxygen;
	}
	public Date getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}
}
