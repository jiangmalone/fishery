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
	public static final int PORT = 5678;
	public static final int POOL_SIZE = 20;
    public static final int FISH=0;
    public static final int LOBSTER=1;
    public static final int CRAB=2;

	private static final String RELOAD_FISH_KEY = "reload_fish";
	public static boolean RELOAD_FISH;
	/*是否开启查询缓存*/
	public static boolean SELECT_CACHE = false;
	
	private static Properties p = null;
	
	static {
		p = new Properties();
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();  
			if (cl == null)
				cl = Constants.class.getClassLoader(); 
			InputStream in = cl.getResourceAsStream("config.properties");
			
			p.load(in);
			RELOAD_FISH = Boolean.valueOf(p.getProperty(RELOAD_FISH_KEY));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
		
}
