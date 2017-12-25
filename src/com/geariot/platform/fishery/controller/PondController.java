package com.geariot.platform.fishery.controller;

import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.geariot.platform.fishery.entities.Pond;
import com.geariot.platform.fishery.service.PondService;

@RestController
@RequestMapping("/pond")
public class PondController {
	
	@Autowired
	private PondService pondService;
	
	@RequestMapping(value = "/addPond" , method = RequestMethod.POST)
	public Map<String,Object> addPond(Pond pond){
		return pondService.addPond(pond);
	}
}
