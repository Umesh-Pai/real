package com.smartz.vt.qrcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by umesh.pai on 01/16/2018.
 */

@SpringBootApplication
public class QRCodeApplication {
	
	public static void main(String[] args) {
		System.out.println("In Spring Boot");
		SpringApplication.run(QRCodeApplication.class, args);
	}
}
