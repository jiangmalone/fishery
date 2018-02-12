/**
 * 
 */
package com.geariot.platform.fishery.model;

/**
 * @author mxy940127
 *
 */
public class PH {
	
	private float ph;
	private String receiveTime;
	public PH(){
		
	}
	public PH(float ph, String receiveTime){
		this.setPh(ph);
		this.receiveTime = receiveTime;
	}
	
	public String getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}
	public float getPh() {
		return ph;
	}
	public void setPh(float ph) {
		this.ph = ph;
	}
}
