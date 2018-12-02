package com.accenture.loginserver.domain;

import java.util.Base64;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.accenture.loginserver.exception.Handlers;
import com.accenture.loginserver.exception.HandlerResult;

@Path("/userinfo")
public class LoginService {

	@Autowired
	private LoginRepository lgr;

	// CREATE
	// http:localhost:8089/userinfo/save
	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUser(LoginUser loginUser) throws JSONException {
		HandlerResult hr = Handlers.getInstance().fieldChecker(loginUser,
				"User Created");
		if (!hr.isError()) {
			lgr.save(loginUser);
			JSONObject json = new JSONObject();
			json.put("INFO", "User Created!");
		}

		return hr.getResponse();

	}
	@GET
	@Path("idfinder/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findById(@PathParam("id")long id) throws JSONException{

		LoginUser user = lgr.findOne(id);

		JSONObject responseJsonObject = new JSONObject();
		JSONObject jsonUser = new JSONObject();

		jsonUser.put("id", user.getId());
		jsonUser.put("userName", user.getUserName());
		jsonUser.put("firstName", user.getFirstName());
		jsonUser.put("lastName", user.getLastName());
		jsonUser.put("email", user.getEmail());
		jsonUser.put("accessType", user.getAccessType());
		jsonUser.put("password", user.getPassword());


		responseJsonObject.put("USER", jsonUser);

		return Response.status(200).entity(responseJsonObject.toString()).type(MediaType.APPLICATION_JSON).build();

	}

	// UPDATE
	@PUT
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUser(LoginUser loginUser)
			throws JSONException {
		JSONObject json = new JSONObject();
		LoginUser user = lgr.findOne(loginUser.getId());

		user.setUserName(loginUser.getUserName());
		user.setPassword(loginUser.getPassword());
		user.setLastName(loginUser.getLastName());
		user.setFirstName(loginUser.getFirstName());
		user.setEmail(loginUser.getEmail());
		user.setAccessType(loginUser.getAccessType());

		HandlerResult hr = Handlers.getInstance().fieldChecker(loginUser,
				"User Updated");
		if (!hr.isError()) {
			lgr.save(user);
			json.put("INFO", "User Updated!");
		}

		return hr.getResponse();
	}
	// http:localhost/userinfo/find
	@GET
	@Path("/find")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAll()
			throws JSONException {
		JSONObject json = new JSONObject();
		List<LoginUser> user =  lgr.findAll();
		if(user != null && user.size() > 0 ){
			json.put("USERS", user);
		}

		return Response.status(200).entity(json.toString())
				.type(MediaType.APPLICATION_JSON).build();
	}

	// @HEADER PARAM
	// http://localhost:8089/userinfo/login
	@POST
	@Path("login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserPassInfo(
			@HeaderParam("Authorization") String authString)
			throws JSONException {

		JSONObject json = new JSONObject();
		if (isUserAuth(authString)) {
			String accessType = getAccessType(authString);
			json.put("RESPONSE", "AUTHORIZED USER");
			json.put("accessType", accessType);

			List<LoginUser> user =  lgr.findAll();
			if(user != null && user.size() > 0 ){
				json.put("USERS", user);
			}

			return Response.status(200).entity(json.toString())
					.type(MediaType.APPLICATION_JSON).build();
		} else {
			json.put("RESPONSE", "UNAUTHORIZED USER");
			return Response.status(403).entity(json.toString())
					.type(MediaType.APPLICATION_JSON).build();
		}
	}

	// DELETE
	@DELETE
	@Path("/delete")
	public Response deleteUSer(LoginUser loginUser) {
		JSONObject json = new JSONObject();
		loginUser = lgr.findByUserName(loginUser.getUserName());

		if (loginUser != null) {
			lgr.delete(loginUser);
			return Response.status(200).entity("USER DELETED!").build();
		} else {
			return Response.status(403).entity(json.toString())
					.type(MediaType.APPLICATION_JSON).build();
		}
	}

	private boolean isUserAuth(String authString) {

		String[] authSplit = authString.split("\\s+");
		String authInfo = authSplit[1];

		byte[] bytes = Base64.getDecoder().decode(authInfo);
		String decoded = new String(bytes);
		String[] cred = decoded.split(":");

		System.out.println(cred);
		LoginUser login = lgr.findByUserNameAndPassword(cred[0], cred[1]);

		if (login != null) {
			return true;
		} else {
			return false;
		}

	}

	private String getAccessType(String authString) {

		String[] authSplit = authString.split("\\s+");
		String authInfo = authSplit[1];

		byte[] bytes = Base64.getDecoder().decode(authInfo);
		String decoded = new String(bytes);
		String[] cred = decoded.split(":");

		System.out.println(cred);
		LoginUser login = lgr.findByUserNameAndPassword(cred[0], cred[1]);

		if (login.getAccessType().equalsIgnoreCase("admin")) {
			return "ADMIN";
		} else {
			return "NON-ADMIN";
		}

	}
}
