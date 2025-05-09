package com.udea.msvc_supples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsvcSupplesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcSupplesApplication.class, args);
	}

}
