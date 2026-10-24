package com.smartnotes.controller;

import com.smartnotes.service.PlantillaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.smartnotes.domain.Plantilla;
import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.Set;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PlantillaController {

    private final PlantillaService plantillaService;

    public PlantillaController(PlantillaService plantillaService) {
        this.plantillaService = plantillaService;
    }

    @GetMapping("/plantillas")
    public String mostrarCatalogo(
            @RequestParam(defaultValue = "") String nombre,
            @RequestParam(defaultValue = "") String perfil,
            @RequestParam(defaultValue = "") String categoria,
            @RequestParam(required = false) Long id,
            Model model) {

        model.addAttribute(
                "plantillas",
                plantillaService.buscarActivas(nombre, perfil, categoria)
        );

        model.addAttribute("nombre", nombre);
        model.addAttribute("perfil", perfil);
        model.addAttribute("categoria", categoria);

        model.addAttribute(
                "seleccionada",
                id == null
                        ? null
                        : plantillaService.buscarPorId(id)
                                .filter(plantilla -> plantilla.isActiva())
                                .orElse(null)
        );

        return "catalogo";
    }

    @GetMapping("/plantillas/nueva")
    public String mostrarFormulario(Model model) {
        Plantilla plantilla = new Plantilla();
        plantilla.setActiva(true);

        model.addAttribute("plantilla", plantilla);

        return "formularioplantilla";
    }

    @GetMapping("/plantillas/administracion")
    public String mostrarAdministracion(Model model) {

        model.addAttribute(
                "plantillas",
                plantillaService.listarTodas()
        );

        return "administracionplantillas";
    }

    @PostMapping("/plantillas/{id}/estado")
    public String cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activa) {

        plantillaService.cambiarEstado(id, activa);

        return "redirect:/plantillas/administracion";
    }

    @GetMapping("/plantillas/{id}/editar")
    public String mostrarEdicion(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "plantilla",
                plantillaService.buscarPorId(id)
                        .orElseThrow(()
                                -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Plantilla no encontrada"
                                )
                        )
        );

        return "formularioplantilla";
    }

    @PostMapping("/plantillas/guardar")
    public String guardarPlantilla(
            @Valid @ModelAttribute("plantilla") Plantilla plantilla,
            BindingResult resultado,
            RedirectAttributes mensajes) {

        // Revisar variables repetidas y variables obligatorias.
        Set<String> nombres = new HashSet<>();

        for (int i = 0; i < plantilla.getVariables().size(); i++) {
            Plantilla.Variable variable = plantilla.getVariables().get(i);

            if (variable == null) {
                resultado.reject(
                        "variable.invalida",
                        "La lista contiene una variable inválida."
                );
                continue;
            }

            String nombreVariable = variable.getNombre();

            if (nombreVariable == null || nombreVariable.isBlank()) {
                continue;
            }

            nombreVariable = nombreVariable.trim();

            if (!nombres.add(nombreVariable)) {
                resultado.rejectValue(
                        "variables[" + i + "].nombre",
                        "variable.repetida",
                        "La variable " + nombreVariable + " está repetida."
                );
            }

            String contenido = plantilla.getContenido();

            if (variable.isObligatoria()
                    && contenido != null
                    && !contenido.isBlank()
                    && !contenido.contains("[" + nombreVariable + "]")) {

                resultado.rejectValue(
                        "contenido",
                        "variable.faltante",
                        "Falta la variable obligatoria ["
                        + nombreVariable + "] en el contenido."
                );
            }
        }

        // Si hay errores, volver al formulario sin guardar.
        if (resultado.hasErrors()) {
            return "formularioplantilla";
        }

        try {
            plantillaService.guardar(plantilla);
        } catch (IllegalArgumentException excepcion) {
            resultado.reject(
                    "plantilla.noEncontrada",
                    excepcion.getMessage()
            );

            return "formularioplantilla";
        }

        mensajes.addFlashAttribute(
                "mensaje",
                "Plantilla guardada temporalmente. "
                + "Los datos se perderán al reiniciar la aplicación."
        );

        return "redirect:/plantillas/administracion";
    }
}
