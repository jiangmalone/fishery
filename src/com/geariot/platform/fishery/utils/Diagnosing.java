package com.geariot.platform.fishery.utils;

import org.springframework.stereotype.Component;

@Component
public class Diagnosing {
	public static  final String[] DOString = {"水体溶氧正常","水体溶氧偏低，及时增氧","水体溶氧极低，请测量氨氮、亚硝酸盐，及时调水"};
	public static final String[] pHString = {"PH正常","PH过低，请使用石灰或换水，并适当追肥","PH过高，换水或添加微生物制剂进行调水","PH极端异常，检查PH传感器是否需要维护保养"};
	public static final String[] WTString = {"水温适宜","水温偏高，控制饲料投喂量，提前调水","水温偏低，请控制饲料投喂量，提前调水"};	


	public String getDiagnosing(String data,float value,int type) {
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
