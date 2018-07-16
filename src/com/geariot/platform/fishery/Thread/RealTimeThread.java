package com.geariot.platform.fishery.Thread;

import org.springframework.beans.factory.annotation.Autowired;

import com.geariot.platform.fishery.service.EquipmentService;

public class RealTimeThread implements Runnable {
	
	@Autowired
	private EquipmentService equipmentService;
	
	@Override
	public void run() {
		//equipmentService.realTimeData(device_sn)

	}

}
