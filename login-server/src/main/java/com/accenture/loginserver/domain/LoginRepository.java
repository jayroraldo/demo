package com.accenture.loginserver.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.String;


import java.util.List;

import com.accenture.loginserver.domain.LoginUser;


public interface LoginRepository extends JpaRepository<LoginUser, Long>{

	LoginUser findByUserNameAndPassword(String username, String password);



	LoginUser findByUserName(String username);

	List<LoginUser> findAll();
}
