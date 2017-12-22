package com.geariot.platform.fishery.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 常量类，存储项目中需要用的常量
 * @author haizhe
 *
 */
public class Constants {

	public static final String RESPONSE_CODE_KEY = "code"; //返回对象里的code的key名称
	public static final String RESPONSE_MSG_KEY = "msg"; //返回对象里的msg的key名称
	public static final String RESPONSE_DATA_KEY = "data"; //返回对象里的data的key名称
	public static final String RESPONSE_SIZE_KEY = "size"; //返回对象里的size的key名称
	public static final String RESPONSE_CLIENT_KEY = "client";
	public static final String RESPONSE_REAL_SIZE_KEY = "realSize";
	public static final String RESPONSE_AMOUNT_KEY = "amount";

	/*是否开启查询缓存*/
	public static boolean SELECT_CACHE = false;
	
	private static Properties p = null;
	
	/*static {
		p = new Properties();
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();  
			if (cl == null)
				cl = Constants.class.getClassLoader(); 
			InputStream in = cl.getResourceAsStream("config.properties");
			
			p.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}*/
	
	
		
}
