package com.geariot.platform.fishery.controller;

import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
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
	public Map<String,Object> addPond(@RequestBody Pond pond){
		return pondService.addPond(pond);
	}
	
	@RequestMapping(value = "/delPonds" , method = RequestMethod.GET)
	public Map<String,Object> delPonds(Integer... pondIds){
		return pondService.delPonds(pondIds);
	}
	
	@RequestMapping(value = "/modifyPond" , method = RequestMethod.POST)
	public Map<String,Object> modifyPond(@RequestBody Pond pond){
		return pondService.modifyPond(pond);
	}
	
	@RequestMapping(value = "/query" , method = RequestMethod.GET)
	public Map<String,Object> queryPond(String relationId, String name, int page, int number){
		return pondService.queryPond(relationId, name, page, number);
	}
	
	@RequestMapping(value = "/pondEquipment" , method = RequestMethod.GET)
	public Map<String,Object> pondEquipment(int pondId, int page, int number){
		return pondService.pondEquipment(pondId, page, number);
	}
	
	@RequestMapping(value = "/fish" , method = RequestMethod.GET)
	public Map<String,Object> fishCateList(){
		return pondService.fishCateList();
	}
	
	@RequestMapping(value = "/wxQuery" , method = RequestMethod.GET)
	public Map<String,Object> WXqueryPond(String relationId){
		return pondService.WXqueryPond(relationId);
	}
	
	@RequestMapping(value = "/pondDetail" , method = RequestMethod.GET)
	public Map<String, Object> pondDetail(int pondId){
		return pondService.pondDetail(pondId);
	}
}
