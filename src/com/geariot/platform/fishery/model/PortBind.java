package com.geariot.platform.fishery.model;

public class PortBind {

	private int port;
	private	String bindName;
	private int bindId;
	private int bindPort;
	private String bindDeviceSn;
	public PortBind(){
		
	}
	public PortBind(int port, String bindName, int bindId, int bindPort, String bindDeviceSn){
		this.port = port;
		this.bindName = bindName;
		this.bindId = bindId;
		this.bindPort = bindPort;
		this.bindDeviceSn = bindDeviceSn;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getBindName() {
		return bindName;
	}
	public void setBindName(String bindName) {
		this.bindName = bindName;
	}
	public int getBindId() {
		return bindId;
	}
	public void setBindId(int bindId) {
		this.bindId = bindId;
	}
	public int getBindPort() {
		return bindPort;
	}
	public void setBindPort(int bindPort) {
		this.bindPort = bindPort;
	}
	public String getBindDeviceSn() {
		return bindDeviceSn;
	}
	public void setBindDeviceSn(String bindDeviceSn) {
		this.bindDeviceSn = bindDeviceSn;
	}
	
}
