package com.deportivo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deportivo.model.ClubEntidad;

public interface ClubRepositorio extends JpaRepository<ClubEntidad, Long> {

	boolean existsByEntrenadorId(Long entrenadorId);

	Optional<ClubEntidad> findByEntrenadorId(Long entrenadorId);
}
