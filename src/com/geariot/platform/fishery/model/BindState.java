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
	
	private int pondId;
	private String pondName;
	private Set<PortBind> portBinds;
	public BindState(){
		
	}
	public BindState(int pondId, String pondName, Set<PortBind> portBinds){
		this.pondId = pondId;
		this.pondName = pondName;
		this.portBinds = portBinds;
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
	
}
