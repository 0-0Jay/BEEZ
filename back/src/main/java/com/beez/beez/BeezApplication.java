package com.beez.beez;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.beez.beez.**.mapper")
public class BeezApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeezApplication.class, args);
	}
}
