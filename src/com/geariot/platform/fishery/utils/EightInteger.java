/**
 * 
 */
package com.geariot.platform.fishery.utils;

import java.util.Random;

/**
 * @author mxy940127
 *
 */
public class EightInteger {

	public static int eightInteger(){
		StringBuilder builder = new StringBuilder();
		Random random = new Random();
		for(int i=0;i<8;i++){
			builder.append(random.nextInt(10));
		}
		return Integer.parseInt(builder.toString());
	}
	
}
