/**
 * 
 */
package com.geariot.platform.fishery.model;

import java.util.Set;

/**
 * @author mxy940127
 *
 */
public class BindState {
	
	private int pondId;			//塘口id
	private String pondName;	//塘口名称
	private String status;		//绑定状态
	private String deviceName;	//设备名称
	private Set<PortBind> portBinds; 	//端口绑定
	public BindState(){
		
	}
	public BindState(int pondId, String pondName, Set<PortBind> portBinds, String status, String deviceName){
		this.pondId = pondId;
		this.pondName = pondName;
		this.portBinds = portBinds;
		this.deviceName = deviceName;
		this.status = status;
	}
	public int getPondId() {
		return pondId;
	}
	public void setPondId(int pondId) {
		this.pondId = pondId;
	}
	public String getPondName() {
		return pondName;
	}
	public void setPondName(String pondName) {
		this.pondName = pondName;
	}
	public Set<PortBind> getPortBinds() {
		return portBinds;
	}
	public void setPortBinds(Set<PortBind> portBinds) {
		this.portBinds = portBinds;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
}
