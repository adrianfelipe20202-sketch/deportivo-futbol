package com.deportivo.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.deportivo.model.AsociacionEntidad;
import com.deportivo.repository.AsociacionRepositorio;
import com.deportivo.util.MensajesPersistenciaNeon;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/asociaciones")
public class AsociacionController {

	private final AsociacionRepositorio asociacionRepositorio;

	public AsociacionController(AsociacionRepositorio asociacionRepositorio) {
		this.asociacionRepositorio = asociacionRepositorio;
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("activeNav", "asociaciones");
		model.addAttribute("asociaciones", asociacionRepositorio.findAll());
		return "asociacion-list";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("activeNav", "asociaciones");
		model.addAttribute("asociacion", new AsociacionEntidad());
		model.addAttribute("editando", false);
		return "asociacion-form";
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
		return asociacionRepositorio.findById(id).map(a -> {
			model.addAttribute("activeNav", "asociaciones");
			model.addAttribute("asociacion", a);
			model.addAttribute("editando", true);
			return "asociacion-form";
		}).orElseGet(() -> {
			ra.addFlashAttribute("mensajeError", "Asociación no encontrada.");
			return "redirect:/asociaciones";
		});
	}

	@PostMapping("/guardar")
	public String guardar(@Valid @ModelAttribute("asociacion") AsociacionEntidad asociacion, BindingResult br,
			Model model, RedirectAttributes ra) {
		if (br.hasErrors()) {
			model.addAttribute("activeNav", "asociaciones");
			model.addAttribute("editando", asociacion.getId() != null);
			return "asociacion-form";
		}
		try {
			AsociacionEntidad saved = asociacionRepositorio.save(asociacion);
			asociacionRepositorio.flush();
			ra.addFlashAttribute("mensajeExito", MensajesPersistenciaNeon.exitoGuardado("Asociación", saved.getId()));
		} catch (DataAccessException ex) {
			ra.addFlashAttribute("mensajeError", MensajesPersistenciaNeon.errorBaseDatos(ex));
		}
		return "redirect:/asociaciones";
	}

	@GetMapping("/{id}/eliminar")
	public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
		try {
			asociacionRepositorio.deleteById(id);
			asociacionRepositorio.flush();
			ra.addFlashAttribute("mensajeExito", MensajesPersistenciaNeon.exitoEliminado("Asociación", id));
		} catch (DataIntegrityViolationException ex) {
			ra.addFlashAttribute("mensajeError", "No se puede eliminar: existen clubes vinculados a esta asociación.");
		} catch (DataAccessException ex) {
			ra.addFlashAttribute("mensajeError", MensajesPersistenciaNeon.errorBaseDatos(ex));
		}
		return "redirect:/asociaciones";
	}
}
