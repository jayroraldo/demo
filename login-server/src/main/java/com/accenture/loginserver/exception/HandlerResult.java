package com.accenture.loginserver.exception;

import javax.ws.rs.core.Response;

public class HandlerResult {

	private Response response;
	private boolean error;

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public HandlerResult() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HandlerResult(Response response, boolean error) {
		super();
		this.response = response;
		this.error = error;
	}

}
