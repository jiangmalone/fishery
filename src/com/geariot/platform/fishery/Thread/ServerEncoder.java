package com.geariot.platform.fishery.Thread;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.geariot.platform.fishery.entities.Sensor;

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
		JSONObject obj = JSONObject.fromObject(sensor);
		return obj.toString();
	}

}
