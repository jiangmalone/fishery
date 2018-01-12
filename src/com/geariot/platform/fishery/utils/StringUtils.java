package com.geariot.platform.fishery.utils;

public class StringUtils {
private static StringBuilder sb=new StringBuilder();



public static StringBuilder add(String deviceSn,int way,byte order) {
	return sb.append("5AA5")
	.append(deviceSn)
	.append(CommonUtils.printHexString((byte)way))
	.append(String.valueOf(order));
}
}
