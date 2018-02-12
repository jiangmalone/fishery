/**
 * 
 */
package com.geariot.platform.fishery.model;

/**
 * @author mxy940127
 *
 */
public class Temperature {
	
	private float temperature;
	private String receiveTime;
	public Temperature(){
		
	}
	public Temperature(float temperature, String receiveTime){
		this.setTemperature(temperature);
		this.receiveTime = receiveTime;
	}
	
	public String getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}
	public float getTemperature() {
		return temperature;
	}
	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}
	
}
