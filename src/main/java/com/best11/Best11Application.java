package com.best11;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Best11Application {

	public static void main(String[] args) {
		System.out.println("JWT_SECRET = " + System.getenv("JWT_SECRET"));
		SpringApplication.run(Best11Application.class, args);
	}

}

//TODO : 공통 응답 래퍼부터 진행하기