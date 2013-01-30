package org.community.grahak.context;

import java.util.Map;

public class ServiceRequestContext implements IServiceRequestContext {

	private String userName;
	private String password;
	private Map<String, String> headers;
	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	@Override
	public String getUserName() {
		// TODO Auto-generated method stub
		return userName;
	}

	@Override
	public void setPassword(final String pwd) {
		this.password = pwd;

	}

	@Override
	public void setUserName(final String userName) {

		this.userName = userName;

	}

	@Override
	public Map<String, String> getHeaders() {
		// TODO Auto-generated method stub
		return this.headers;
	}

	@Override
	public void setHeaders(final Map<String, String> headers) {
		this.headers = headers;
	}

}
