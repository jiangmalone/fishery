/**
 * 
 */
package com.geariot.platform.fishery.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author mxy940127
 *
 */
@Entity
public class AeratorStatus {
	
	private String device_sn;
	private int way;
	private boolean isTimed;
	private boolean on_off;
	@Id
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
	public boolean isTimed() {
		return isTimed;
	}
	public void setTimed(boolean isTimed) {
		this.isTimed = isTimed;
	}
	public boolean isOn_off() {
		return on_off;
	}
	public void setOn_off(boolean on_off) {
		this.on_off = on_off;
	}
	
}
