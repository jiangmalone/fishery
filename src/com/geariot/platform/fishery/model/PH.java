/**
 * 
 */
package com.geariot.platform.fishery.model;

import java.util.Date;

/**
 * @author mxy940127
 *
 */
public class PH {
	
	private float ph;
	private Date receiveTime;
	public PH(){
		
	}
	public PH(float ph, Date receiveTime){
		this.setPh(ph);
		this.receiveTime = receiveTime;
	}
	
	public Date getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}
	public float getPh() {
		return ph;
	}
	public void setPh(float ph) {
		this.ph = ph;
	}
}
