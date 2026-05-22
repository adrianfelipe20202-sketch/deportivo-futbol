package com.deportivo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DeportivoApplication {

	public static void main(String[] args) {
		// Perfil activo: siempre neon (ver ForceNeonEnvironmentPostProcessor).
		SpringApplication.run(DeportivoApplication.class, args);
	}
}
