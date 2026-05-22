package com.deportivo.controller;

import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.deportivo.model.EntrenadorEntidad;
import com.deportivo.repository.ClubRepositorio;
import com.deportivo.repository.EntrenadorRepositorio;
import com.deportivo.util.MensajesPersistenciaNeon;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/entrenadores")
public class EntrenadorController {

	private final EntrenadorRepositorio entrenadorRepositorio;
	private final ClubRepositorio clubRepositorio;

	public EntrenadorController(EntrenadorRepositorio entrenadorRepositorio, ClubRepositorio clubRepositorio) {
		this.entrenadorRepositorio = entrenadorRepositorio;
		this.clubRepositorio = clubRepositorio;
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("activeNav", "entrenadores");
		model.addAttribute("entrenadores", entrenadorRepositorio.findAll());
		model.addAttribute("entrenadoresOcupadosIds",
				clubRepositorio.findAll().stream().map(c -> c.getEntrenador().getId()).collect(Collectors.toSet()));
		return "entrenador-list";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("activeNav", "entrenadores");
		model.addAttribute("entrenador", new EntrenadorEntidad());
		model.addAttribute("editando", false);
		return "entrenador-form";
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
		return entrenadorRepositorio.findById(id).map(e -> {
			model.addAttribute("activeNav", "entrenadores");
			model.addAttribute("entrenador", e);
			model.addAttribute("editando", true);
			return "entrenador-form";
		}).orElseGet(() -> {
			ra.addFlashAttribute("mensajeError", "Entrenador no encontrado.");
			return "redirect:/entrenadores";
		});
	}

	@PostMapping("/guardar")
	public String guardar(@Valid @ModelAttribute("entrenador") EntrenadorEntidad entrenador, BindingResult br,
			Model model, RedirectAttributes ra) {
		if (br.hasErrors()) {
			model.addAttribute("activeNav", "entrenadores");
			model.addAttribute("editando", entrenador.getId() != null);
			return "entrenador-form";
		}
		try {
			EntrenadorEntidad saved = entrenadorRepositorio.save(entrenador);
			entrenadorRepositorio.flush();
			ra.addFlashAttribute("mensajeExito", MensajesPersistenciaNeon.exitoGuardado("Entrenador", saved.getId()));
		} catch (DataAccessException ex) {
			ra.addFlashAttribute("mensajeError", MensajesPersistenciaNeon.errorBaseDatos(ex));
		}
		return "redirect:/entrenadores";
	}

	@GetMapping("/{id}/eliminar")
	public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
		try {
			entrenadorRepositorio.deleteById(id);
			entrenadorRepositorio.flush();
			ra.addFlashAttribute("mensajeExito", MensajesPersistenciaNeon.exitoEliminado("Entrenador", id));
		} catch (DataAccessException ex) {
			ra.addFlashAttribute("mensajeError", MensajesPersistenciaNeon.errorBaseDatos(ex));
		}
		return "redirect:/entrenadores";
	}
}
