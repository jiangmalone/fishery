package com.geariot.platform.fishery.model;

public class BrokenMSG {

	 private static ThreadLocal<StringBuilder> brokenMSG = new ThreadLocal<StringBuilder>();

	 private StringBuilder sb=new StringBuilder();
	    public StringBuilder getMSG() {
	        return brokenMSG.get();
	    }

	    public void setMSG(String msg) {
	    	sb.append(msg);
	    	sb.append(" ");
	    	brokenMSG.set(sb);
	    }

	    public void clear() {
	    	brokenMSG.remove();;
	    }
	
}
 