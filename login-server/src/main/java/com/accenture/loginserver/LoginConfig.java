package com.accenture.loginserver;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import com.accenture.loginserver.domain.LoginService;


@Configuration
public class LoginConfig extends ResourceConfig {


	public LoginConfig(){
		register(LoginService.class);
	}
}
