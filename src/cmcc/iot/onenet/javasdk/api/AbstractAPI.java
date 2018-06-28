package cmcc.iot.onenet.javasdk.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import cmcc.iot.onenet.javasdk.request.RequestInfo.Method;

public abstract class AbstractAPI <T>{
	public String key;
	public String url;
	public Method method;
    public ObjectMapper mapper = new ObjectMapper();
}
