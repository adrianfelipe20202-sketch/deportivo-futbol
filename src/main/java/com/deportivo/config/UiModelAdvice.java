package com.deportivo.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class UiModelAdvice {

	/** URL base de la app (host + puerto reales de la petición). */
	@ModelAttribute("appOrigin")
	public String appOrigin(HttpServletRequest request) {
		if (request == null) {
			return "";
		}
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getLocalPort();
	}

	@ModelAttribute("resumenPerfilDb")
	public String resumenPerfilDb() {
		return "Neon · PostgreSQL";
	}
}
