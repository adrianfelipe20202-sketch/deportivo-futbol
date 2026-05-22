package com.deportivo.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Fuerza el único perfil soportado: {@code neon}. Cualquier {@code -Dspring.profiles.active=…}
 * u otra variable queda anulada para que la app no arranque contra otra base por error.
 */
public class ForceNeonEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		environment.setActiveProfiles("neon");
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}
}
