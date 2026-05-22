package com.deportivo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.deportivo.repository.AsociacionRepositorio;
import com.deportivo.repository.ClubRepositorio;
import com.deportivo.repository.CompeticionRepositorio;
import com.deportivo.repository.EntrenadorRepositorio;
import com.deportivo.repository.JugadorRepositorio;

@Controller
public class DashboardController {

	private final EntrenadorRepositorio entrenadorRepositorio;
	private final AsociacionRepositorio asociacionRepositorio;
	private final CompeticionRepositorio competicionRepositorio;
	private final JugadorRepositorio jugadorRepositorio;
	private final ClubRepositorio clubRepositorio;

	public DashboardController(EntrenadorRepositorio entrenadorRepositorio, AsociacionRepositorio asociacionRepositorio,
			CompeticionRepositorio competicionRepositorio, JugadorRepositorio jugadorRepositorio,
			ClubRepositorio clubRepositorio) {
		this.entrenadorRepositorio = entrenadorRepositorio;
		this.asociacionRepositorio = asociacionRepositorio;
		this.competicionRepositorio = competicionRepositorio;
		this.jugadorRepositorio = jugadorRepositorio;
		this.clubRepositorio = clubRepositorio;
	}

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("activeNav", "dashboard");
		model.addAttribute("totalEntrenadores", entrenadorRepositorio.count());
		model.addAttribute("totalAsociaciones", asociacionRepositorio.count());
		model.addAttribute("totalCompeticiones", competicionRepositorio.count());
		model.addAttribute("totalJugadores", jugadorRepositorio.count());
		model.addAttribute("totalClubes", clubRepositorio.count());
		return "index";
	}
}
