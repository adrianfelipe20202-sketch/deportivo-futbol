package com.deportivo.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

/**
 * Carga {@code NEON_DB_PASSWORD} desde archivos locales si no está en variables de entorno.
 * Rutas (en orden): {@code config/supabase-db.properties}, {@code %USERPROFILE%/.deportivo/supabase-db.properties}.
 */
public class NeonLocalCredentialsPostProcessor implements EnvironmentPostProcessor, Ordered {

	private static final List<String> CANDIDATE_KEYS = List.of(
			"NEON_DB_PASSWORD",
			"spring.datasource.password");

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		if (StringUtils.hasText(environment.getProperty("NEON_DB_PASSWORD"))) {
			return;
		}

		Map<String, Object> overrides = new LinkedHashMap<>();
		loadFile(Path.of("config", "supabase-db.properties"), overrides);
		String home = System.getenv("USERPROFILE");
		if (home == null || home.isBlank()) {
			home = System.getProperty("user.home");
		}
		if (StringUtils.hasText(home)) {
			loadFile(Path.of(home, ".deportivo", "supabase-db.properties"), overrides);
		}

		if (!overrides.isEmpty()) {
			environment.getPropertySources().addFirst(new MapPropertySource("neonLocalCredentials", overrides));
		}
	}

	private static void loadFile(Path path, Map<String, Object> overrides) {
		if (!Files.isRegularFile(path)) {
			return;
		}
		Properties props = new Properties();
		try (InputStream in = Files.newInputStream(path)) {
			props.load(in);
		} catch (IOException ignored) {
			return;
		}
		for (String key : CANDIDATE_KEYS) {
			String value = props.getProperty(key);
			if (StringUtils.hasText(value)) {
				overrides.put("NEON_DB_PASSWORD", value.trim());
				return;
			}
		}
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 10;
	}
}
