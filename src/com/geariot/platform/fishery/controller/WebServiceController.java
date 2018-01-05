package com.geariot.platform.fishery.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.geariot.platform.fishery.utils.HttpRequest;


@RestController
@RequestMapping("/webService")
public class WebServiceController {
	
	private String mapApiKey = "1fb41392b17b0575e03b5374cb7b029f";
	
	private String weatherUrl = "http://restapi.amap.com/v3/weather/weatherInfo";
	
	private String weatherExtensions ="all";
	
	private String ContentType = "application/json";
	
	@RequestMapping(value = "/weather" , method = RequestMethod.POST)
	public String getWeatherTest(String city){
		Map<String,Object> param = new HashMap<>();
		param.put("city", city);
		param.put("extensions",weatherExtensions);
		param.put("key", mapApiKey);
		Map<String, Object> head = setWeatherHead();
		String result = HttpRequest.getCall(weatherUrl, param, head);
		return result;
		
	}

	private Map<String, Object> setWeatherHead() {
		Map<String,Object> head = new HashMap<String, Object>();
		head.put("Content-Type", ContentType);
		return null;
	}
	
	
}
