package org.community.grahak.context;

import java.util.Map;

public interface IServiceRequestContext {
	
	public void setUserName(String userName);
	public String getUserName();
	public void setPassword(String pwd);
	public String getPassword();
	public Map<String, String> getHeaders();
	public void setHeaders(Map<String, String> headers);
	
}
