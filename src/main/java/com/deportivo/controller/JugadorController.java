package com.deportivo.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.deportivo.model.ClubEntidad;
import com.deportivo.model.JugadorEntidad;
import com.deportivo.repository.ClubRepositorio;
import com.deportivo.repository.JugadorRepositorio;
import com.deportivo.util.MensajesPersistenciaNeon;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/jugadores")
public class JugadorController {

	private final JugadorRepositorio jugadorRepositorio;
	private final ClubRepositorio clubRepositorio;

	public JugadorController(JugadorRepositorio jugadorRepositorio, ClubRepositorio clubRepositorio) {
		this.jugadorRepositorio = jugadorRepositorio;
		this.clubRepositorio = clubRepositorio;
	}

	@GetMapping
	@Transactional(readOnly = true)
	public String listar(Model model) {
		model.addAttribute("activeNav", "jugadores");
		model.addAttribute("jugadores", jugadorRepositorio.findAll());
		return "jugador-list";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("activeNav", "jugadores");
		JugadorEntidad j = new JugadorEntidad();
		j.setClub(new ClubEntidad());
		model.addAttribute("jugador", j);
		model.addAttribute("clubes", clubRepositorio.findAll());
		model.addAttribute("editando", false);
		return "jugador-form";
	}

	@GetMapping("/{id}/editar")
	@Transactional(readOnly = true)
	public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
		return jugadorRepositorio.findById(id).map(j -> {
			model.addAttribute("activeNav", "jugadores");
			model.addAttribute("jugador", j);
			model.addAttribute("clubes", clubRepositorio.findAll());
			model.addAttribute("editando", true);
			return "jugador-form";
		}).orElseGet(() -> {
			ra.addFlashAttribute("mensajeError", "Jugador no encontrado.");
			return "redirect:/jugadores";
		});
	}

	@PostMapping("/guardar")
	public String guardar(@Valid @ModelAttribute("jugador") JugadorEntidad jugador, BindingResult br,
			Model model, RedirectAttributes ra) {
		if (jugador.getClub() == null || jugador.getClub().getId() == null) {
			br.rejectValue("club", "NotNull", "Debe seleccionar un club.");
		} else {
			clubRepositorio.findById(jugador.getClub().getId()).ifPresent(jugador::setClub);
		}
		if (br.hasErrors()) {
			model.addAttribute("activeNav", "jugadores");
			model.addAttribute("clubes", clubRepositorio.findAll());
			model.addAttribute("editando", jugador.getId() != null);
			return "jugador-form";
		}
		try {
			JugadorEntidad saved = jugadorRepositorio.save(jugador);
			jugadorRepositorio.flush();
			ra.addFlashAttribute("mensajeExito", MensajesPersistenciaNeon.exitoGuardado("Jugador", saved.getId()));
		} catch (DataAccessException ex) {
			ra.addFlashAttribute("mensajeError", MensajesPersistenciaNeon.errorBaseDatos(ex));
		}
		return "redirect:/jugadores";
	}

	@GetMapping("/{id}/eliminar")
	public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
		try {
			jugadorRepositorio.deleteById(id);
			jugadorRepositorio.flush();
			ra.addFlashAttribute("mensajeExito", MensajesPersistenciaNeon.exitoEliminado("Jugador", id));
		} catch (DataAccessException ex) {
			ra.addFlashAttribute("mensajeError", MensajesPersistenciaNeon.errorBaseDatos(ex));
		}
		return "redirect:/jugadores";
	}
}
