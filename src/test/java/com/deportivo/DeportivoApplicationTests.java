package com.deportivo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = DeportivoApplication.class)
@ActiveProfiles("neon")
@EnabledIfEnvironmentVariable(named = "NEON_DB_PASSWORD", matches = ".+")
class DeportivoApplicationTests {

	@Test
	void contextLoads() {
	}
}
