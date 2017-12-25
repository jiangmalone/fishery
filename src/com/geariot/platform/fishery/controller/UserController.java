package com.geariot.platform.fishery.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.geariot.platform.fishery.entities.Company;
import com.geariot.platform.fishery.entities.WXUser;
import com.geariot.platform.fishery.service.UserService;

@RestController
@RequestMapping(value = "/usermanagement")
public class UserController {

	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/addCompany" , method = RequestMethod.POST)
	public Map<String, Object> addCompany(Company company){
		return userService.addCompany(company);
	}
	@RequestMapping(value = "/addWXUser" , method = RequestMethod.POST)
	public Map<String, Object> addWXUser(WXUser wxuser){
		return userService.addWXUser(wxuser);
	}
	
	@RequestMapping(value = "/modifyCompany" , method = RequestMethod.POST)
	public Map<String,Object> modifyCompany(Company company){
		return userService.modifyCompany(company);
	}

	@RequestMapping(value = "/modifyWXUser" , method = RequestMethod.POST)
	public Map<String,Object> modifyWXUser(WXUser wxuser){
		return userService.modifyWXUser(wxuser);
	}
	
	@RequestMapping(value = "/delCompany" , method = RequestMethod.POST)
	public Map<String,Object> deleteCompany(Integer... companyIds ){
		return userService.deleteCompany(companyIds);
	}
	@RequestMapping(value = "/delWXUser" , method = RequestMethod.POST)
	public Map<String,Object> deleteWXUser(Integer... WXUserIds ){
		return userService.deleteWXUser(WXUserIds);
	}
	
	@RequestMapping(value = "/queryCompany" , method = RequestMethod.GET)
	public Map<String,Object> queryCompany(String name , int page , int number){
		return userService.queryCompany(name, page, number);
	}
	
	@RequestMapping(value = "/queryWXUser" , method = RequestMethod.GET)
	public Map<String,Object> queryWXUser(String name , int page , int number){
		return userService.queryWXUser(name, page, number);
	}
	
	
}