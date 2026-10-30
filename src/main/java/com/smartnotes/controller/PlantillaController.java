package com.smartnotes.controller;

import com.smartnotes.domain.Categoria;
import com.smartnotes.domain.Plantilla;
import com.smartnotes.domain.Rol;
import com.smartnotes.dto.PlantillaFormulario;
import com.smartnotes.repository.CategoriaRepository;
import com.smartnotes.repository.RolRepository;
import com.smartnotes.service.PlantillaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PlantillaController {

    private final PlantillaService plantillaService;
    private final CategoriaRepository categoriaRepository;
    private final RolRepository rolRepository;

    public PlantillaController(
            PlantillaService plantillaService,
            CategoriaRepository categoriaRepository,
            RolRepository rolRepository) {

        this.plantillaService = plantillaService;
        this.categoriaRepository = categoriaRepository;
        this.rolRepository = rolRepository;
    }

    @InitBinder("plantilla")
    public void configurarFormulario(WebDataBinder binder) {
        binder.setAutoGrowCollectionLimit(50);

        binder.setAllowedFields(
                "idPlantilla",
                "nombre",
                "descripcion",
                "tipoAsociado",
                "idCategoria",
                "idsRoles",
                "contenido",
                "activo",
                "requiereEvidencia",
                "variables[*].nombre",
                "variables[*].descripcion",
                "variables[*].obligatoria"
        );
    }

    @ModelAttribute("categoriasDisponibles")
    public List<Categoria> cargarCategorias() {
        return categoriaRepository.findByActivoTrue();
    }

    @ModelAttribute("rolesDisponibles")
    public List<Rol> cargarRoles() {
        return rolRepository.findAll();
    }

    @ModelAttribute("esAdministrador")
    public boolean esAdministrador(Authentication autenticacion) {
        return tieneRol(autenticacion, "ROLE_ADMIN");
    }

    @GetMapping("/plantillas")
    public String mostrarCatalogo(
            @RequestParam(defaultValue = "") String nombre,
            @RequestParam(defaultValue = "") String perfil,
            @RequestParam(defaultValue = "") String categoria,
            @RequestParam(required = false) Integer id,
            Authentication autenticacion,
            Model model) {

        List<Plantilla> disponibles = plantillaService
                .buscarActivas(nombre, perfil, categoria)
                .stream()
                .filter(plantilla ->
                        puedeUsar(plantilla, autenticacion)
                )
                .toList();

        model.addAttribute("plantillas", disponibles);
        model.addAttribute("nombre", nombre);
        model.addAttribute("perfil", perfil);
        model.addAttribute("categoria", categoria);

        Plantilla seleccionada = id == null
                ? null
                : plantillaService.buscarPorId(id)
                        .filter(Plantilla::isActivo)
                        .filter(plantilla ->
                                puedeUsar(plantilla, autenticacion)
                        )
                        .orElse(null);

        model.addAttribute("seleccionada", seleccionada);

        return "catalogo";
    }

    @GetMapping("/plantillas/nueva")
    public String mostrarFormulario(
            Authentication autenticacion,
            Model model) {

        exigirAdministrador(autenticacion);

        model.addAttribute(
                "plantilla",
                new PlantillaFormulario()
        );

        return "formularioplantilla";
    }

    @GetMapping("/plantillas/administracion")
    public String mostrarAdministracion(
            Authentication autenticacion,
            Model model) {

        exigirAdministrador(autenticacion);

        model.addAttribute(
                "plantillas",
                plantillaService.listarTodas()
        );

        return "administracionplantillas";
    }

    @GetMapping("/plantillas/{id}/editar")
    public String mostrarEdicion(
            @PathVariable Integer id,
            Authentication autenticacion,
            Model model) {

        exigirAdministrador(autenticacion);

        if (plantillaService.buscarPorId(id).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Plantilla no encontrada."
            );
        }

        model.addAttribute(
                "plantilla",
                plantillaService.obtenerFormulario(id)
        );

        return "formularioplantilla";
    }

    @PostMapping("/plantillas/{id}/estado")
    public String cambiarEstado(
            @PathVariable Integer id,
            @RequestParam boolean activa,
            Authentication autenticacion,
            RedirectAttributes mensajes) {

        exigirAdministrador(autenticacion);

        try {
            plantillaService.cambiarEstado(id, activa);
        } catch (IllegalArgumentException excepcion) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    excepcion.getMessage()
            );
        }

        mensajes.addFlashAttribute(
                "mensaje",
                activa
                        ? "Plantilla activada."
                        : "Plantilla desactivada."
        );

        return "redirect:/plantillas/administracion";
    }

    @PostMapping("/plantillas/guardar")
    public String guardarPlantilla(
            @Valid @ModelAttribute("plantilla")
            PlantillaFormulario formulario,
            BindingResult resultado,
            Authentication autenticacion,
            RedirectAttributes mensajes) {

        exigirAdministrador(autenticacion);

        if (resultado.hasErrors()) {
            return "formularioplantilla";
        }

        try {
            plantillaService.guardar(
                    formulario,
                    autenticacion.getName()
            );
        } catch (IllegalArgumentException excepcion) {
            resultado.reject(
                    "plantilla.invalida",
                    excepcion.getMessage()
            );

            return "formularioplantilla";
        } catch (DataIntegrityViolationException excepcion) {
            resultado.reject(
                    "plantilla.integridad",
                    "No se pudo guardar. Revisa si el nombre de la "
                            + "plantilla o de alguna variable ya existe."
            );

            return "formularioplantilla";
        }

        mensajes.addFlashAttribute(
                "mensaje",
                "Plantilla guardada correctamente."
        );

        return "redirect:/plantillas/administracion";
    }

    private boolean tieneRol(
            Authentication autenticacion,
            String autoridad) {

        return autenticacion != null
                && autenticacion.isAuthenticated()
                && autenticacion.getAuthorities().stream()
                        .anyMatch(rol ->
                                autoridad.equals(rol.getAuthority())
                        );
    }

    private void exigirAdministrador(
            Authentication autenticacion) {

        if (!esAdministrador(autenticacion)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo los administradores pueden realizar esta acción."
            );
        }
    }

    private boolean puedeUsar(
            Plantilla plantilla,
            Authentication autenticacion) {

        if (esAdministrador(autenticacion)) {
            return true;
        }

        return plantilla.getRoles().stream()
                .anyMatch(rol ->
                        tieneRol(
                                autenticacion,
                                "ROLE_" + rol.getNombre()
                        )
                );
    }
}