package com.deportivo.controller;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.deportivo.model.AsociacionEntidad;
import com.deportivo.model.ClubEntidad;
import com.deportivo.model.CompeticionEntidad;
import com.deportivo.model.EntrenadorEntidad;
import com.deportivo.repository.AsociacionRepositorio;
import com.deportivo.repository.ClubRepositorio;
import com.deportivo.repository.CompeticionRepositorio;
import com.deportivo.repository.EntrenadorRepositorio;
import com.deportivo.util.MensajesPersistenciaNeon;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/clubes")
public class ClubController {

	private final ClubRepositorio clubRepositorio;
	private final EntrenadorRepositorio entrenadorRepositorio;
	private final AsociacionRepositorio asociacionRepositorio;
	private final CompeticionRepositorio competicionRepositorio;

	public ClubController(ClubRepositorio clubRepositorio, EntrenadorRepositorio entrenadorRepositorio,
			AsociacionRepositorio asociacionRepositorio, CompeticionRepositorio competicionRepositorio) {
		this.clubRepositorio = clubRepositorio;
		this.entrenadorRepositorio = entrenadorRepositorio;
		this.asociacionRepositorio = asociacionRepositorio;
		this.competicionRepositorio = competicionRepositorio;
	}

	@GetMapping
	@Transactional(readOnly = true)
	public String listar(Model model) {
		model.addAttribute("activeNav", "clubes");
		model.addAttribute("clubes", clubRepositorio.findAll());
		return "club-list";
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("activeNav", "clubes");
		model.addAttribute("club", clubShell());
		model.addAttribute("editando", false);
		model.addAttribute("competicionesSeleccionadas", Collections.emptySet());
		cargarCatalogos(model, null);
		return "club-form";
	}

	@GetMapping("/{id}/editar")
	@Transactional(readOnly = true)
	public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
		return clubRepositorio.findById(id).map(club -> {
			club.getCompeticiones().size();
			club.getJugadores().size();
			model.addAttribute("activeNav", "clubes");
			model.addAttribute("club", club);
			model.addAttribute("editando", true);
			model.addAttribute("competicionesSeleccionadas",
					club.getCompeticiones().stream().map(CompeticionEntidad::getId).collect(Collectors.toSet()));
			cargarCatalogos(model, club.getId());
			return "club-form";
		}).orElseGet(() -> {
			ra.addFlashAttribute("mensajeError", "Club no encontrado.");
			return "redirect:/clubes";
		});
	}

	@PostMapping("/guardar")
	public String guardar(@Valid @ModelAttribute("club") ClubEntidad club, BindingResult br,
			@RequestParam(required = false) List<Long> competicionIds, Model model, RedirectAttributes ra) {

		Long eid = club.getEntrenador() != null ? club.getEntrenador().getId() : null;
		Long aid = club.getAsociacion() != null ? club.getAsociacion().getId() : null;
		if (eid == null) {
			br.rejectValue("entrenador", "error.required", "Seleccione un entrenador.");
		}
		if (aid == null) {
			br.rejectValue("asociacion", "error.required", "Seleccione una asociación.");
		}
		if (eid != null) {
			Optional<ClubEntidad> otro = clubRepositorio.findByEntrenadorId(eid);
			if (otro.isPresent() && (club.getId() == null || !otro.get().getId().equals(club.getId()))) {
				br.rejectValue("entrenador", "error.assigned", "Ese entrenador ya está asignado a otro club.");
			}
		}
		if (br.hasErrors()) {
			model.addAttribute("activeNav", "clubes");
			model.addAttribute("editando", club.getId() != null);
			Set<Long> sel = competicionIds == null ? Collections.emptySet() : new HashSet<>(competicionIds);
			model.addAttribute("competicionesSeleccionadas", sel);
			cargarCatalogos(model, club.getId());
			return "club-form";
		}

		EntrenadorEntidad ent = entrenadorRepositorio.findById(eid).orElseThrow();
		AsociacionEntidad asoc = asociacionRepositorio.findById(aid).orElseThrow();

		try {
			ClubEntidad saved;
			if (club.getId() == null) {
				ClubEntidad nuevo = new ClubEntidad();
				nuevo.setNombre(club.getNombre());
				nuevo.setEntrenador(ent);
				nuevo.setAsociacion(asoc);
				aplicarCompeticiones(nuevo, competicionIds);
				saved = clubRepositorio.save(nuevo);
			} else {
				ClubEntidad existente = clubRepositorio.findById(club.getId()).orElseThrow();
				existente.setNombre(club.getNombre());
				existente.setEntrenador(ent);
				existente.setAsociacion(asoc);
				aplicarCompeticiones(existente, competicionIds);
				saved = clubRepositorio.save(existente);
			}
			clubRepositorio.flush();
			ra.addFlashAttribute("mensajeExito", MensajesPersistenciaNeon.exitoGuardado("Club", saved.getId()));
		} catch (DataAccessException ex) {
			ra.addFlashAttribute("mensajeError", MensajesPersistenciaNeon.errorBaseDatos(ex));
		}
		return "redirect:/clubes";
	}

	@GetMapping("/{id}/eliminar")
	public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
		try {
			clubRepositorio.deleteById(id);
			clubRepositorio.flush();
			ra.addFlashAttribute("mensajeExito", MensajesPersistenciaNeon.exitoEliminado("Club", id));
		} catch (DataAccessException ex) {
			ra.addFlashAttribute("mensajeError", MensajesPersistenciaNeon.errorBaseDatos(ex));
		}
		return "redirect:/clubes";
	}

	private ClubEntidad clubShell() {
		ClubEntidad c = new ClubEntidad();
		c.setEntrenador(new EntrenadorEntidad());
		c.setAsociacion(new AsociacionEntidad());
		return c;
	}

	private void aplicarCompeticiones(ClubEntidad destino, List<Long> ids) {
		destino.getCompeticiones().clear();
		if (ids != null) {
			for (Long cid : ids) {
				competicionRepositorio.findById(cid).ifPresent(comp -> destino.getCompeticiones().add(comp));
			}
		}
	}

	private void cargarCatalogos(Model model, Long clubEditId) {
		List<EntrenadorEntidad> entrenadores = entrenadorRepositorio.findAll().stream()
				.filter(e -> {
					Optional<ClubEntidad> ocupado = clubRepositorio.findByEntrenadorId(e.getId());
					if (ocupado.isEmpty()) {
						return true;
					}
					return clubEditId != null && ocupado.get().getId().equals(clubEditId);
				})
				.collect(Collectors.toList());
		model.addAttribute("entrenadores", entrenadores);
		model.addAttribute("asociaciones", asociacionRepositorio.findAll());
		model.addAttribute("competicionesCatalogo", competicionRepositorio.findAll());
	}
}
