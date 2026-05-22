package com.deportivo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deportivo.model.CompeticionEntidad;

public interface CompeticionRepositorio extends JpaRepository<CompeticionEntidad, Long> {
}
