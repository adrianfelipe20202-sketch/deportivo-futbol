package com.deportivo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deportivo.model.JugadorEntidad;

public interface JugadorRepositorio extends JpaRepository<JugadorEntidad, Long> {

	List<JugadorEntidad> findByClubIdOrderByNumeroAsc(Long clubId);
}
