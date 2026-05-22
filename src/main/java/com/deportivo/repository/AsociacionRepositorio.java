package com.deportivo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deportivo.model.AsociacionEntidad;

public interface AsociacionRepositorio extends JpaRepository<AsociacionEntidad, Long> {
}
