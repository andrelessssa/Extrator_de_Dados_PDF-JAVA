package com.arsal.Extrator.de.Diarias;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication (exclude = {DataSourceAutoConfiguration.class})
public class ExtratorDeDiariasApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExtratorDeDiariasApplication.class, args);
	}

}
