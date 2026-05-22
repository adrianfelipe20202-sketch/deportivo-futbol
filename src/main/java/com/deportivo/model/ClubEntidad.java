package com.deportivo.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "club")
public class ClubEntidad {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(nullable = false, length = 160)
	private String nombre;

	@NotNull
	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "id_entrenador", nullable = false, unique = true)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private EntrenadorEntidad entrenador;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_asociacion", nullable = false)
	private AsociacionEntidad asociacion;

	@OneToMany(mappedBy = "club", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<JugadorEntidad> jugadores = new ArrayList<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "club_competicion", joinColumns = @JoinColumn(name = "id_club"), inverseJoinColumns = @JoinColumn(name = "id_competicion"))
	private Set<CompeticionEntidad> competiciones = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public EntrenadorEntidad getEntrenador() {
		return entrenador;
	}

	public void setEntrenador(EntrenadorEntidad entrenador) {
		this.entrenador = entrenador;
	}

	public AsociacionEntidad getAsociacion() {
		return asociacion;
	}

	public void setAsociacion(AsociacionEntidad asociacion) {
		this.asociacion = asociacion;
	}

	public List<JugadorEntidad> getJugadores() {
		return jugadores;
	}

	public void setJugadores(List<JugadorEntidad> jugadores) {
		this.jugadores = jugadores;
	}

	public Set<CompeticionEntidad> getCompeticiones() {
		return competiciones;
	}

	public void setCompeticiones(Set<CompeticionEntidad> competiciones) {
		this.competiciones = competiciones;
	}
}
