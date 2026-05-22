package com.deportivo.controller;

import java.time.LocalDate;

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

import com.deportivo.model.CompeticionEntidad;
import com.deportivo.repository.CompeticionRepositorio;
import com.deportivo.util.MensajesPersistenciaNeon;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/competiciones")
public class CompeticionController {

	private final CompeticionRepositorio competicionRepositorio;

	public CompeticionController(CompeticionRepositorio competicionRepositorio) {
		this.competicionRepositorio = competicionRepositorio;
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("activeNav", "competiciones");
		model.addAttribute("competiciones", competicionRepositorio.findAll());
		return "competicion-list";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("activeNav", "competiciones");
		CompeticionEntidad c = new CompeticionEntidad();
		c.setFechaInicio(LocalDate.now());
		c.setFechaFin(LocalDate.now().plusMonths(2));
		c.setMontoPremio(10000.0);
		model.addAttribute("competicion", c);
		model.addAttribute("editando", false);
		return "competicion-form";
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
		return competicionRepositorio.findById(id).map(c -> {
			model.addAttribute("activeNav", "competiciones");
			model.addAttribute("competicion", c);
			model.addAttribute("editando", true);
			return "competicion-form";
		}).orElseGet(() -> {
			ra.addFlashAttribute("mensajeError", "Competición no encontrada.");
			return "redirect:/competiciones";
		});
	}

	@PostMapping("/guardar")
	public String guardar(@Valid @ModelAttribute("competicion") CompeticionEntidad competicion, BindingResult br,
			Model model, RedirectAttributes ra) {
		if (br.hasErrors()) {
			model.addAttribute("activeNav", "competiciones");
			model.addAttribute("editando", competicion.getId() != null);
			return "competicion-form";
		}
		try {
			CompeticionEntidad saved = competicionRepositorio.save(competicion);
			competicionRepositorio.flush();
			ra.addFlashAttribute("mensajeExito", MensajesPersistenciaNeon.exitoGuardado("Competición", saved.getId()));
		} catch (DataAccessException ex) {
			ra.addFlashAttribute("mensajeError", MensajesPersistenciaNeon.errorBaseDatos(ex));
		}
		return "redirect:/competiciones";
	}

	@GetMapping("/{id}/eliminar")
	public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
		try {
			competicionRepositorio.deleteById(id);
			competicionRepositorio.flush();
			ra.addFlashAttribute("mensajeExito", MensajesPersistenciaNeon.exitoEliminado("Competición", id));
		} catch (DataIntegrityViolationException ex) {
			ra.addFlashAttribute("mensajeError", "No se puede eliminar: la competición está enlazada a clubes.");
		} catch (DataAccessException ex) {
			ra.addFlashAttribute("mensajeError", MensajesPersistenciaNeon.errorBaseDatos(ex));
		}
		return "redirect:/competiciones";
	}
}
