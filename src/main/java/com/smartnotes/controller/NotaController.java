package com.smartnotes.controller;

import com.smartnotes.dto.NotaGeneradorForm;
import com.smartnotes.service.NotaCorreccionService;
import com.smartnotes.service.PlantillaService;
import com.smartnotes.service.TeamMemberService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/persona3")
public class NotaController {

    private final PlantillaService plantillaService;
    private final TeamMemberService teamMemberService;
    private final NotaCorreccionService notaCorreccionService;

    public NotaController(
            PlantillaService plantillaService,
            TeamMemberService teamMemberService,
            NotaCorreccionService notaCorreccionService) {
        this.plantillaService = plantillaService;
        this.teamMemberService = teamMemberService;
        this.notaCorreccionService = notaCorreccionService;
    }

    @GetMapping({"", "/generador"})
    public String mostrarGenerador(Model model) {
        model.addAttribute("plantillas", plantillaService.findAllActive());
        model.addAttribute("miembros", teamMemberService.findAll());
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new NotaGeneradorForm());
        }
        return "persona3/generador";
    }

    @PostMapping("/generador/preview")
    public String preview(@Valid @ModelAttribute("form") NotaGeneradorForm form,
            BindingResult bindingResult,
            Model model) {
        model.addAttribute("plantillas", plantillaService.findAllActive());
        model.addAttribute("miembros", teamMemberService.findAll());

        if (bindingResult.hasErrors()) {
            return "persona3/generador";
        }

        model.addAttribute("preview", notaCorreccionService.generarVistaPrevia(form));
        return "persona3/generador";
    }

    @PostMapping("/generador/guardar")
    public String guardar(@Valid @ModelAttribute("form") NotaGeneradorForm form,
            BindingResult bindingResult,
            Model model) {
        model.addAttribute("plantillas", plantillaService.findAllActive());
        model.addAttribute("miembros", teamMemberService.findAll());

        if (bindingResult.hasErrors()) {
            return "persona3/generador";
        }

        try {
            var nota = notaCorreccionService.guardar(form);
            return "redirect:/persona3/notas/" + nota.getIdNota();
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "persona3/generador";
        }
    }

    @GetMapping("/notas")
    public String listarNotas(Model model) {
        model.addAttribute("notas", notaCorreccionService.findAll());
        return "persona3/lista";
    }

    @GetMapping("/notas/{id}")
    public String detalleNota(@PathVariable Integer id, Model model) {
        model.addAttribute("nota", notaCorreccionService.findById(id));
        return "persona3/detalle";
    }

    @GetMapping("/notas/{id}/editar")
    public String editarNota(@PathVariable Integer id, Model model) {
        var nota = notaCorreccionService.findById(id);
        NotaGeneradorForm form = new NotaGeneradorForm();
        form.setPlantillaId(nota.getPlantilla().getIdPlantilla());
        form.setTeamMemberId(nota.getTeamMember().getIdTeamMember());
        form.setFechaInicio(nota.getFechaError());
        form.setFechaFin(nota.getFechaEmision());
        form.setDescripcion(nota.getDescripcionError());
        form.setCorreccion(nota.getCorreccionRealizada());
        form.setEvidencia(nota.getEvidenciaObjetiva());
        model.addAttribute("form", form);
        model.addAttribute("plantillas", plantillaService.findAllActive());
        model.addAttribute("miembros", teamMemberService.findAll());
        model.addAttribute("notaId", id);
        return "persona3/generador";
    }

    @PostMapping("/notas/{id}/editar")
    public String actualizarNota(@PathVariable Integer id,
            @Valid @ModelAttribute("form") NotaGeneradorForm form,
            BindingResult bindingResult,
            Model model) {
        model.addAttribute("plantillas", plantillaService.findAllActive());
        model.addAttribute("miembros", teamMemberService.findAll());

        if (bindingResult.hasErrors()) {
            model.addAttribute("notaId", id);
            return "persona3/generador";
        }

        try {
            notaCorreccionService.actualizar(id, form);
            return "redirect:/persona3/notas/" + id;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("notaId", id);
            return "persona3/generador";
        }
    }

    @PostMapping("/notas/{id}/eliminar")
    public String eliminarNota(@PathVariable Integer id) {
        notaCorreccionService.eliminar(id);
        return "redirect:/persona3/notas";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/persona3/generador";
    }
}
