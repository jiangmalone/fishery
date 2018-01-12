package com.geariot.platform.fishery.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class FishCateList {

	private static List<String> fish_cate;
	
	private static final String FILENAME = "fish_cate.txt";
		
	private static final Logger log = LogManager.getLogger(FishCateList.class);
	
	public static List<String>getFishNames()
	{
		if(fish_cate == null)
		{
			synchronized(FishCateList.class)
			{
				if(fish_cate == null)
				{
					fish_cate = new ArrayList<String>();
					ClassLoader cl = Thread.currentThread().getContextClassLoader();  
					if (cl == null)
						cl = FishCateList.class.getClassLoader(); 
					File file = new File(cl.getResource(FILENAME).getPath());
	                if(file.isFile() && file.exists()){ //判断文件是否存在
	                	try
	                	{
		                    InputStreamReader read = new InputStreamReader(
		                    new FileInputStream(file));
		                    BufferedReader bufferedReader = new BufferedReader(read);
		                    String lineTxt = null;
		                    while((lineTxt = bufferedReader.readLine()) != null){
		                        log.debug("fish_cate name:" + lineTxt + "###");
		                        fish_cate.add(lineTxt);
		                    }
		                    read.close();
	                	}
	                	catch(Exception e)
	                	{
	                		log.error("读取文件出错");
	                		e.printStackTrace();
	                	}
	                }else{
	                	log.error("找不到指定的文件");
	                }
	        
				}
			}
		}
		return fish_cate;
	}
	
	public static void main(String[] args)
	{
		getFishNames();
	}
}
