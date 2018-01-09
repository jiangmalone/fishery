package com.geariot.platform.fishery.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class CommonUtils {

	public static final Random RANDOM;
	static {
		RANDOM = new Random();
	}

	public static String generateUUID() {

		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * 检测字符串是否不为空(null,"","null")
	 * 
	 * @param s
	 * @return 不为空则返回true，否则返回false
	 */
	public static boolean isNotEmpty(String s) {
		return s != null && !"".equals(s) && !"null".equals(s);
	}

	/**
	 * 检测字符串是否为空(null,"","null")
	 * 
	 * @param s
	 * @return 为空则返回true，不否则返回false
	 */
	public static boolean isEmpty(String s) {
		return s == null || "".equals(s) || "null".equals(s);
	}

	public static String printHexString(byte b) {
		String hex = Integer.toHexString(b & 0xFF);
		if (hex.length() == 1) {
			hex = '0' + hex;
		}
		return hex.toUpperCase();
	}

	public static int bytesToInt(byte[] src) {
		int value;
		value = (int) ((src[2] & 0xFF) | ((src[1] & 0xFF) << 8) | ((src[0] & 0xFF) << 16));
		return value;
	}

	public static byte[] getByteArray(int i) {
		byte[] b = new byte[3];
		// b[0] = (byte) ((i & 0xff000000) >> 24);
		b[0] = (byte) ((i & 0x00ff0000) >> 16);
		b[1] = (byte) ((i & 0x0000ff00) >> 8);
		b[2] = (byte) (i & 0x000000ff);
		return b;
	}

	public static int getInt(byte[] bytes) {
		return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)) | (0xff0000 & (bytes[2] << 16))
				| (0xff000000 & (bytes[3] << 24));
	}

	public static float getFloat(byte[] bytes) {
		return Float.intBitsToFloat(getInt(bytes));
	}

	public static float byte2float(byte[] b, int index) {
		int l;
		l = b[index + 0];
		l &= 0xff;
		l |= ((long) b[index + 1] << 8);
		l &= 0xffff;
		l |= ((long) b[index + 2] << 16);
		l &= 0xffffff;
		l |= ((long) b[index + 3] << 24);
		return Float.intBitsToFloat(l);
	}

	public static byte[] getByteArray(float f) {
		int intbits = Float.floatToIntBits(f);// 将float里面的二进制串解释为int整数
		return getByteArray(intbits);
	}

	public static Date stringToDate(String strTime, String formatType) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(formatType);
		Date date = null;
		date = formatter.parse(strTime);
		return date;
	}

	public static void arrayHandle(byte[] source, byte[] target, int sourceStart, int targetStart, int num) {
	    while(num!=0) {
	    	target[targetStart]	=source[sourceStart];
	    	targetStart++;
	    	sourceStart++;
	    	num--;
	    }
	}
	
	public static String printHexStringMerge(byte[] source,int start,int num) {
		String target=null;
		while(num!=0) {
			target=printHexString(source[start]);
			start++;
			num--;
		}
		return target;
	}
	
	public static byte arrayMerge(byte[] source,int start,int num) {
		byte target=0;
		while(num!=0) {
			target=source[start];
			start++;
			num--;
		}
		return target;
	}
}
