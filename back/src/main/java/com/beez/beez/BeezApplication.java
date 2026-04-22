package com.beez.beez;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.beez.beez.**.mapper")
@EnableAsync
public class BeezApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeezApplication.class, args);
	}
}
