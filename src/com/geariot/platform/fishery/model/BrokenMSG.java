package com.geariot.platform.fishery.model;

public class BrokenMSG {

	 private static ThreadLocal<StringBuilder> brokenMSG = new ThreadLocal<StringBuilder>();

	 private static StringBuilder sb=new StringBuilder();
	    public StringBuilder getMSG() {
	        return brokenMSG.get();
	    } 

	    public void setMSG(String msg) {
	    	sb.append(msg);
	    	sb.append(" ");
	    	brokenMSG.set(sb);
	    }

	    public void clear() {
	    	//sb=new StringBuilder();
	    	sb.delete(0, sb.length());//清空原先的sb
	    	brokenMSG.remove();;
	    }
	
}
 