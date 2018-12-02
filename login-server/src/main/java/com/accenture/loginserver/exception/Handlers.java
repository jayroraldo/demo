package com.accenture.loginserver.exception;

import java.util.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import com.accenture.loginserver.domain.LoginUser;

public class Handlers {

	private static Handlers handlers;

	private Handlers() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static Handlers getInstance() {

		if (handlers == null) {
			handlers = new Handlers();
		}
		return handlers;
	}

	public HandlerResult fieldChecker(LoginUser loginUser, String successMSG)
			throws JSONException {

		boolean error = false;

		List<String> nullFields = new ArrayList<>();
		nullFields.clear();

		if (loginUser.getFirstName() == null) {
			nullFields.add("Fist Name");
			error = true;

		}
		if (loginUser.getLastName() == null) {
			nullFields.add("Last Name");
			error = true;
		}
		if (loginUser.getUserName() == null) {
			nullFields.add("Username");
			error = true;
		}
		if (loginUser.getAccessType() == null) {
			nullFields.add("Access Type");
			error = true;
		}
		if (loginUser.getPassword() == null) {
			nullFields.add("Password");
			error = true;
		}
		if (loginUser.getEmail() == null) {
			nullFields.add("email");
			error = true;
		}
		JSONObject json = new JSONObject();
		if (nullFields.size() <= 0) {
			json.put("Result", successMSG);
		} else {
			String result = "Input for ";
			result += String.join(", ", nullFields);
			result += " is REQUIRED!";

			json.put("Result", result);
		}

		return new HandlerResult(Response.status(200).entity(json.toString())
				.type(MediaType.APPLICATION_JSON).build(), error);
	}

}
