package com.geariot.platform.fishery.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

@Component
public class Diagnosing {
	public Map<String, Object> getDiagnosing() {
		Properties prop = new Properties();
		ResourceBundle resource = ResourceBundle.getBundle("diagnosing");
		String[] DOString = new String[3];
		String[] pHString = new String[4];
		String[] WTString = new String[3];
		String[] peakAndValleyAnalysis = new String[3];
		try {
			WTString[0] = new String(resource.getString("WTString0").getBytes("ISO-8859-1"),"utf-8");
			WTString[1] = new String(resource.getString("WTString1").getBytes("ISO-8859-1"),"utf-8");
			WTString[2] = new String(resource.getString("WTString2").getBytes("ISO-8859-1"),"utf-8");
			DOString[0] = new String(resource.getString("DOString0").getBytes("ISO-8859-1"),"utf-8");
			DOString[1] = new String(resource.getString("DOString1").getBytes("ISO-8859-1"),"utf-8");
			DOString[2] = new String(resource.getString("DOString2").getBytes("ISO-8859-1"),"utf-8");
			pHString[0] = new String(resource.getString("pHString0").getBytes("ISO-8859-1"),"utf-8");
			pHString[1] = new String(resource.getString("pHString1").getBytes("ISO-8859-1"),"utf-8");
			pHString[2] = new String(resource.getString("pHString2").getBytes("ISO-8859-1"),"utf-8");
			pHString[3] = new String(resource.getString("pHString3").getBytes("ISO-8859-1"),"utf-8");
			peakAndValleyAnalysis[0] = new String(resource.getString("peakAndValleyAnalysis0").getBytes("ISO-8859-1"),"utf-8");
			peakAndValleyAnalysis[1] = new String(resource.getString("peakAndValleyAnalysis1").getBytes("ISO-8859-1"),"utf-8");
			peakAndValleyAnalysis[2] = new String(resource.getString("peakAndValleyAnalysis2").getBytes("ISO-8859-1"),"utf-8");
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, Object> diagnosing = new HashMap<>();
		diagnosing.put("DOString", DOString);
		diagnosing.put("pHString",pHString);
		diagnosing.put("WTString", WTString);
		diagnosing.put("peakAndValleyAnalysis", peakAndValleyAnalysis);
		return diagnosing;
	}
	
	
	public String getDiagnosing(String data,float value,int type) {
		Map<String, Object> diagnosingMap = getDiagnosing();
		String[] WTString = (String[]) diagnosingMap.get("WTString");
		String[] pHString = (String[]) diagnosingMap.get("pHString");
		String[] DOString = (String[]) diagnosingMap.get("DOString");
		String diagnosing = null;
		if(type == 0) {//鱼
			if(data.equals("WT")) {
				if(value>10 &&value <30) {
					diagnosing = WTString[0];
				}else if(value>30) {
					diagnosing = WTString[1];
				}else if(value<10){
					diagnosing = WTString[2];
				}	
				
			}else if(data.equals("pH")) {
				if(value>6.5 &&value <9) {
					diagnosing = pHString[0];
				}else if(value>4.5 &&value <6.5) {
					diagnosing = pHString[1];
				}else if(value>9 && value <10.2){
					diagnosing = pHString[2];
				}else {
					diagnosing = pHString[3];
				}
				
			}else if(data.equals("DO")) {
				if(value>4) {
					diagnosing = DOString[0];
				}else if(value>2&& value <=4) {
					diagnosing = DOString[1];
				}else{
					diagnosing = DOString[2];
				}
			}
		}else if(type == 1) {//虾
			if(data.equals("WT")) {
				if(value>18 &&value <30) {
					diagnosing = WTString[0];
				}else if(value<=18){
					diagnosing = WTString[2];
				}	
				
			}else if(data.equals("pH")) {
				if(value>7.8 &&value <8.5) {
					diagnosing = pHString[0];
				}else if(value>6.5 &&value <=7.8) {
					diagnosing = pHString[1];
				}else if(value>=8.5 && value <9.2){
					diagnosing = pHString[2];
				}else {
					diagnosing = pHString[3];
				}
				
			}else if(data.equals("DO")) {
				if(value>5) {
					diagnosing = DOString[0];
				}else if(value>2&& value <=5) {
					diagnosing = DOString[1];
				}else{
					diagnosing = DOString[2];
				}
			}
		}else {//蟹
			if(data.equals("WT")) {
				if(value>18 &&value <30) {
					diagnosing = WTString[0];
				}else if(value<=18){
					diagnosing = WTString[2];
				}	
				
			}else if(data.equals("pH")) {
				if(value>6.8 &&value <8.3) {
					diagnosing = pHString[0];
				}else if(value>6 &&value <=6.8) {
					diagnosing = pHString[1];
				}else if(value>=8.3 && value <9){
					diagnosing = pHString[2];
				}else {
					diagnosing = pHString[3];
				}
				
			}else if(data.equals("DO")) {
				if(value>5) {
					diagnosing = DOString[0];
				}else if(value>2.5&& value <=5) {
					diagnosing = DOString[1];
				}else{
					diagnosing = DOString[2];
				}
			}
		}
			
		return diagnosing;
	}
	
}
