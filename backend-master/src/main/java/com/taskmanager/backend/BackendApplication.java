package com.taskmanager.backend;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone;

@SpringBootApplication
public class BackendApplication {

	@PostConstruct
	public void init() {
		// This forces the JVM to use UTC, solving the "Asia/Calcutta" fatal error
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		System.out.println("--- System: TimeZone set to UTC ---");
	}

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}
}