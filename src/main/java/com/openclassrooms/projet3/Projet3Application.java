package com.openclassrooms.projet3;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class Projet3Application {

	public static void main(String[] args) {
		SpringApplication.run(Projet3Application.class, args);
	}

}
