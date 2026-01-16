package com.sarojini.MyGardenCare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyGardenCareApplication {

	public static void main(String[] args) {
		System.out.println("DB_URL = " + System.getenv("DB_URL"));
		System.out.println("DB_USERNAME = " + System.getenv("DB_USERNAME"));
		SpringApplication.run(MyGardenCareApplication.class, args);
	}

}
