package com.geariot.platform.fishery.entities;

public class Param {
	private String device_sn;
	private int way;
	private Timer[] timers;
	public String getDevice_sn() {
		return device_sn;
	}
	public void setDevice_sn(String device_sn) {
		this.device_sn = device_sn;
	}
	public int getWay() {
		return way;
	}
	public void setWay(int way) {
		this.way = way;
	}
	public Timer[] getTimers() {
		return timers;
	}
	public void setTimers(Timer[] timers) {
		this.timers = timers;
	}
	
}
