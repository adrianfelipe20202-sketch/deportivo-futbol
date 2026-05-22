package com.deportivo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deportivo.model.EntrenadorEntidad;

public interface EntrenadorRepositorio extends JpaRepository<EntrenadorEntidad, Long> {
}
