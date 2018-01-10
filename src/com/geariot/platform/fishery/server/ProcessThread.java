package com.geariot.platform.fishery.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;



public class ProcessThread extends Thread{
	
	private Socket soc ; 
	private boolean exitFlag;
	
	//private Map<String, PhnBean>map ; 
	
	//private List<IpBean>ls;
	
	private static Logger logger = Logger.getLogger(ProcessThread.class); 
	
	public ProcessThread(Socket s)
	{
		this.soc = s;
		exitFlag = false;
		//map = MonitorMgr.phnMap;
		//ls = MonitorMgr.sortedIp;
	}
	
	public void setFlag(boolean flag)
	{
		this.exitFlag = flag;
	}
	
	public void run()
	{
		InputStream in = null;
		while(!exitFlag)
		{
			byte[]bytes = new byte[255];
        	try {
				in = soc.getInputStream();
				in.read(bytes);
	        	String msg = new String(bytes).trim();
	        	logger.info("received msg:" + msg);
	        	//process(msg, soc);
			}catch(SocketException e)
			{
				logger.info("client socket is closed");
				exitFlag = true;
				try {
					this.join();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} 
        	catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
		}
	}
	
	/*public void process(String msg, Socket s)
	{
		InetAddress address = s.getInetAddress();
		String ip = address.getHostAddress();
		if(msg.equals("")||  !msg.startsWith("{"))
		{
			try {
				exitFlag = true;
				this.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		JSONObject obj = JSONObject.fromObject(msg);
        String sel = (String) obj.get(Constants.COMMAND_LABEL);
        if(sel.equals(Constants.LOGIN_LABEL))
        {
        	PhnBean bean = map.get(ip);
        	if(bean == null)
        	{
        		bean = new PhnBean();
        	}
        	bean.setIp(ip);
        	logger.info("message from ip:" + ip);
        	bean.setName(address.getHostAddress());
        	bean.setS(s);
        	bean.setFlashStatus(Constants.OFF_LABEL);
        	
        	
        	String[]array = ip.split("\\.");
        	
        	if(array.length != 4)
        	{
        		logger.error("ip:" + ip + " is not correct; the length of ip parts is:" + array.length);
        		return;
        	}
        	synchronized(MonitorMgr.obj)
        	{
        		*//**
        		 * temporarily use binary insert algorithm
        		 *//*
        		IpBean ipBean = new IpBean(ip);
        		BinaryInsertAlg.insertIp(ipBean, ls);
        		bean.setIpBean(ipBean);
        		map.put(ip, bean);
        	}
        }
        else if(sel.equals(Constants.LOGOUT_LABEL))
        {
        	Socket soc = map.get(ip).getS();
        	CmdUtils.sendCmd(soc, "done");
        	PhnBean phnBean = map.get(ip);
        	if(phnBean != null)
        	{
        		IpBean ipBean = phnBean.getIpBean();
        		ls.remove(ipBean);
        	}
        	map.remove(ip);
        }
	}*/
	
}
