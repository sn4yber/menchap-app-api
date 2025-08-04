package com.snayber.api_jdbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class ApiJdbcApplication {
	public static void main(String[] args) {
		SpringApplication.run(ApiJdbcApplication.class, args);
	}
}
