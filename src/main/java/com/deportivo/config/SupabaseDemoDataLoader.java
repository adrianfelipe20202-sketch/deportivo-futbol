package com.deportivo.config;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.deportivo.model.AsociacionEntidad;
import com.deportivo.model.ClubEntidad;
import com.deportivo.model.CompeticionEntidad;
import com.deportivo.model.EntrenadorEntidad;
import com.deportivo.model.JugadorEntidad;
import com.deportivo.repository.AsociacionRepositorio;
import com.deportivo.repository.ClubRepositorio;
import com.deportivo.repository.CompeticionRepositorio;
import com.deportivo.repository.EntrenadorRepositorio;
import com.deportivo.repository.JugadorRepositorio;

/**
 * Rellena datos demo la primera vez (perfil {@code neon}) si no hay clubes.
 * Si ya hay clubes, no hace nada.
 */
@Component
@Profile("neon")
@Order(100)
public class SupabaseDemoDataLoader implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(SupabaseDemoDataLoader.class);

	private final ClubRepositorio clubRepositorio;
	private final AsociacionRepositorio asociacionRepositorio;
	private final EntrenadorRepositorio entrenadorRepositorio;
	private final CompeticionRepositorio competicionRepositorio;
	private final JugadorRepositorio jugadorRepositorio;

	public SupabaseDemoDataLoader(ClubRepositorio clubRepositorio, AsociacionRepositorio asociacionRepositorio,
			EntrenadorRepositorio entrenadorRepositorio, CompeticionRepositorio competicionRepositorio,
			JugadorRepositorio jugadorRepositorio) {
		this.clubRepositorio = clubRepositorio;
		this.asociacionRepositorio = asociacionRepositorio;
		this.entrenadorRepositorio = entrenadorRepositorio;
		this.competicionRepositorio = competicionRepositorio;
		this.jugadorRepositorio = jugadorRepositorio;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (clubRepositorio.count() > 0) {
			return;
		}

		AsociacionEntidad asoc = new AsociacionEntidad();
		asoc.setNombre("Federación Demo");
		asoc.setPais("España");
		asoc.setPresidente("María López");
		asoc = asociacionRepositorio.save(asoc);

		EntrenadorEntidad ent = new EntrenadorEntidad();
		ent.setNombre("Carlos");
		ent.setApellido("García");
		ent.setEdad(42);
		ent.setNacionalidad("España");
		ent = entrenadorRepositorio.save(ent);

		CompeticionEntidad copa = new CompeticionEntidad();
		copa.setNombre("Copa Demo 2026");
		copa.setMontoPremio(50_000.0);
		copa.setFechaInicio(LocalDate.of(2026, 1, 10));
		copa.setFechaFin(LocalDate.of(2026, 6, 30));
		copa = competicionRepositorio.save(copa);

		ClubEntidad club = new ClubEntidad();
		club.setNombre("Club Atlético Demo");
		club.setEntrenador(ent);
		club.setAsociacion(asoc);
		club.setCompeticiones(new HashSet<>(Set.of(copa)));
		club = clubRepositorio.save(club);

		JugadorEntidad j1 = new JugadorEntidad();
		j1.setNombre("Ana");
		j1.setApellido("Ruiz");
		j1.setNumero(10);
		j1.setPosicion("Mediocentro");
		j1.setClub(club);
		jugadorRepositorio.save(j1);

		JugadorEntidad j2 = new JugadorEntidad();
		j2.setNombre("Luis");
		j2.setApellido("Martín");
		j2.setNumero(9);
		j2.setPosicion("Delantero");
		j2.setClub(club);
		jugadorRepositorio.save(j2);

		log.info("Supabase: datos demo insertados (1 club, 2 jugadores, 1 competición, 1 asociación, 1 entrenador).");
	}
}
