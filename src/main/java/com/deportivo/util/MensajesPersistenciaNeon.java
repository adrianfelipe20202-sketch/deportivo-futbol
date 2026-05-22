package com.deportivo.util;

import org.springframework.dao.DataAccessException;

/**
 * Textos claros tras escribir en Neon: éxito con id verificable o error real de JDBC.
 */
public final class MensajesPersistenciaNeon {

	private MensajesPersistenciaNeon() {
	}

	public static String exitoGuardado(String tipoEntidad, Long id) {
		return "Confirmado en Neon: " + tipoEntidad + " persistido con id=" + id
				+ ". Si no lo ves en el panel de Neon, pulsa actualizar en la tabla.";
	}

	public static String exitoEliminado(String tipoEntidad, Long id) {
		return "Confirmado en Neon: " + tipoEntidad + " eliminado (id=" + id + ").";
	}

	public static String errorBaseDatos(DataAccessException ex) {
		Throwable root = ex.getMostSpecificCause();
		String detalle = root != null ? root.getMessage() : ex.getMessage();
		return "NO se guardó en Neon (falló la base de datos). Detalle: " + detalle;
	}
}
