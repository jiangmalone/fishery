package com.geariot.platform.fishery.Thread;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.alibaba.fastjson.JSON;
import com.geariot.platform.fishery.entities.Sensor;
import com.google.gson.JsonObject;

import net.sf.json.JSONObject;

public class ServerEncoder implements Encoder.Text<Sensor> {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(EndpointConfig arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public String encode(Sensor sensor) throws EncodeException {
		// TODO Auto-generated method stub
		/*String returnStr = "{\\wt_status\\:"+sensor.getWT_status()
							+",\\device_sn\\:"+sensor.getDevice_sn()
							+",\\id\\:"+sensor.getId()
							+",\\name\\:"+sensor.getName()
							+",\\oxygen\\:"+sensor.getOxygen()
							+",\\oxygen_status\\:"+sensor.getOxygen_status()
							+",\\pH_status\\:"+sensor.getpH_status()
							+",\\pH_value\\:"+sensor.getpH_value()
							+",\\pondId\\:"+sensor.getPondId()
							+",\\port_status\\:"+sensor.getPort_status()
							+",\\relation\\:"+sensor.getRelation()
							+",\\status\\:"+sensor.getStatus()
							+",\\water_temperature\\:"+sensor.getWater_temperature()
							+",\\wayStatus\\:"+sensor.getWayStatus()+"}";*/
		JSONObject obj = JSONObject.fromObject(sensor);
		obj.put("wt_status", sensor.getWT_status());
				
		return obj.toString();
	}

}
