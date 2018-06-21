package com.geariot.platform.fishery.entities;

public class controllerParam {
	private Controller controller;
	private int key;//0表示关闭，1表示打开
	public Controller getController() {
		return controller;
	}
	public void setController(Controller controller) {
		this.controller = controller;
	}
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	

}
