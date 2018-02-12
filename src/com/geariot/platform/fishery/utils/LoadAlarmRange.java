package com.geariot.platform.fishery.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.geariot.platform.fishery.model.AlarmRange;





public class LoadAlarmRange {
	private static final String FILENAME = "AlarmRange.json";
	
	private static final Logger log = LogManager.getLogger(LoadAlarmRange.class);
	 
	public static AlarmRange getAlarmRange() {
        log.debug("准备从AlarmRange.json文件中读取数据");
		String data = readJson(FILENAME);
		log.debug("已读完");
		//System.out.println(data.toString());
		
		//JSONObject jsonObj = JSONObject.parseObject(data)fromObject(data);
		
		//AlarmRange ar = (AlarmRange) JSONObject.toBean(jsonObj, AlarmRange.class);
	    return JSON.parseObject(data, new TypeReference<AlarmRange>() {});
	}
	
	  public static String readJson(String path){
	       
		  ClassLoader cl = Thread.currentThread().getContextClassLoader();  
			if (cl == null)
				cl = LoadAlarmRange.class.getClassLoader(); 
		  //从给定位置获取文件
	        File file = new File(cl.getResource(path).getPath());
	        BufferedReader reader = null;
	        //返回值,使用StringBuffer
	        StringBuffer data = new StringBuffer();
	        //
	        try {
	            reader = new BufferedReader(new FileReader(file));
	            //每次读取文件的缓存
	            String temp = null;
	            while((temp = reader.readLine()) != null){
	                data.append(temp);
	            }
	        } catch (FileNotFoundException e) {
	           log.error("找不到指定文件");
	        } catch (IOException e) {
	           log.error("读取指定文件出错");
	        }finally {
	            //关闭文件流
	            if (reader != null){
	                try {
	                    reader.close();
	                } catch (IOException e) {
	                    log.error("关闭指定文件流出错");
	                }
	            }
	        }
	        return data.toString();
	    }
	  public static void main(String[] args) {
		  long start=System.currentTimeMillis();
		  AlarmRange ar=getAlarmRange();
		  long end=System.currentTimeMillis();
		  System.out.println(ar.getCrab_DO_high_limit()+"   消耗了"+(end-start));
	}
}
