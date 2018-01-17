package com.geariot.platform.fishery.utils;

public class StringUtils {
 



public static StringBuilder add(String deviceSn,int way,int i) {
	StringBuilder sb=new StringBuilder();
	return sb.append("5AA5")
	.append(deviceSn)
	.append(CommonUtils.printHexString((byte)way))
	.append(CommonUtils.printHexString((byte)i));
}
}
