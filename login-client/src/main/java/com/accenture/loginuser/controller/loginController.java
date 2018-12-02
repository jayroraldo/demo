package com.accenture.loginuser.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.accenture.loginuser.domain.LoginUser;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@Controller
public class loginController {

	@GetMapping("/")
	public String loginForm() {
		return "login/index";
	}

	@RequestMapping("/deletepage")
	public String deleteUser(HttpServletRequest request) {

		JSONObject json = new JSONObject();
		json.put("userName", request.getParameter("username"));
		Client client = new Client();

		WebResource wb = client
				.resource("http://localhost:8089/userinfo/delete");
		wb.type(MediaType.APPLICATION_JSON).delete(ClientResponse.class,
				json.toString());

		return "crud/delete";
	}

	@RequestMapping("createpage")
	public String createpage(){
		return "crud/create";
	}

	@PostMapping("/createMethod")
	public String createUser(HttpServletRequest request) {
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		String email = request.getParameter("email");
		String accessType = request.getParameter("accessType");
		JSONObject json = new JSONObject();
		json.put("firstName", firstName);
		json.put("lastName", lastName);
		json.put("userName", userName);
		json.put("password", password);
		json.put("email", email);
		json.put("accessType", accessType);

		Client client = new Client();

		WebResource wb = client
				.resource("http://localhost:8089/userinfo/create");
		wb.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,
				json.toString());

		return "crud/create";

	}

	@RequestMapping(value="/updatepage")
	public String updateUser(@PathParam("id")String id,HttpServletRequest request, Model model) {

		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		String email = request.getParameter("email");
		String accessType = request.getParameter("accessType");
		JSONObject json = new JSONObject();

		json.put("firstName", firstName);
		json.put("firstName", firstName);
		json.put("lastName", lastName);
		json.put("userName", userName);
		json.put("password", password);
		json.put("email", email);
		json.put("accessType", accessType);

		Client client = new Client();
		WebResource wb = client
				.resource("http://localhost:8089/userinfo/update");
		wb.type(MediaType.APPLICATION_JSON).put(ClientResponse.class,
				json.toString());

		return "crud/update";

	}

	@RequestMapping(value="/fillup/{id}")
	public String fillUp(@PathVariable("id")String id,Model model) throws JsonParseException, JsonMappingException, IOException {


		Client client = new Client();
		WebResource wb = client
				.resource("http://localhost:8089/userinfo/idfinder/"+id);


		ClientResponse response = wb.get(ClientResponse.class);
		String responseString = response.getEntity(String.class);
		System.out.println("RESPONSE STRING RECVD FROM THE SEVER : " + responseString);
		ObjectMapper mapper = new ObjectMapper();
		JSONObject userJsonObject = new JSONObject(responseString);
		JSONObject userinnedJson = userJsonObject.getJSONObject("USER");
		LoginUser user = mapper.readValue(userinnedJson.toString().getBytes(), LoginUser.class);
		model.addAttribute("USERTOUPDATE", user);
		return "crud/update";

	}

	@PostMapping(value = "/login")
	public String userLogin(@RequestParam(name = "username") String userName,
			@RequestParam(name = "password") String password, Model model,
			HttpServletResponse hRespo) throws JsonParseException,
			JsonMappingException, IOException {

		String credentials = userName + ":" + password;
		System.out.println(credentials);
		String encodedCreds = Base64.getEncoder().encodeToString(
				credentials.getBytes());
		String passedValue = "Basic " + encodedCreds;
		Client client = new Client();
		WebResource wrResource = client
				.resource("http://localhost:8089/userinfo/login");
		ClientResponse response = wrResource.header("Authorization",
				passedValue).post(ClientResponse.class);

		String restStr = response.getEntity(String.class);
		JSONObject json = new JSONObject(restStr);

		if (json.getString("RESPONSE").equalsIgnoreCase("UNAUTHORIZED USER")) {
			model.addAttribute("error","Invalid Username/Password");
			return "login/index";
		}
		if (json.getString("accessType").equals("NON-ADMIN")) {
			return "admin/nonadminpage";
		}	
		return "admin/adminpage";

	}
}
