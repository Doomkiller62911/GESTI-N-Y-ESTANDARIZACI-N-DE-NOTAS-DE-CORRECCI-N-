package com.smartnotes.controller;

import com.smartnotes.dto.NotaGeneradorForm;
import com.smartnotes.service.NotaCorreccionService;
import com.smartnotes.service.PlantillaService;
import com.smartnotes.service.TeamMemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import com.smartnotes.service.UsuarioService;
import java.security.Principal;

@Controller
@RequestMapping("/Generador_Notas")
public class NotaController {

    private final PlantillaService plantillaService;
    private final TeamMemberService teamMemberService;
    private final NotaCorreccionService notaCorreccionService;
    private final UsuarioService usuarioService;

    public NotaController(
            PlantillaService plantillaService,
            TeamMemberService teamMemberService,
            NotaCorreccionService notaCorreccionService,
            UsuarioService usuarioService) {

        this.plantillaService = plantillaService;
        this.teamMemberService = teamMemberService;
        this.notaCorreccionService = notaCorreccionService;
        this.usuarioService = usuarioService;
    }

    @ModelAttribute
    public void prepararUsuarioCabecera(
            Principal principal,
            Model model) {

        if (principal == null) {
            return;
        }

        usuarioService.getUsuarioPorUsername(principal.getName())
                .ifPresent(usuario -> {
                    model.addAttribute(
                            "nombreUsuarioSesion",
                            usuario.getNombre()
                            + " "
                            + usuario.getApellidoPaterno()
                    );

                    model.addAttribute(
                            "puestoUsuarioSesion",
                            usuario.getPuesto()
                    );

                    model.addAttribute(
                            "rolUsuarioSesion",
                            usuario.getRol().getNombre()
                    );
                });
    }

    // Abrir el generador, opcionalmente con una plantilla seleccionada.
    @GetMapping({"", "/generador"})
    public String mostrarGenerador(
            @RequestParam(required = false) Integer plantillaId,
            Authentication autenticacion,
            Model model) {

        model.addAttribute(
                "plantillas",
                plantillaService.findAllActive()
        );

        model.addAttribute(
                "miembros",
                teamMemberService.findAll()
        );

        if (!model.containsAttribute("form")) {
            NotaGeneradorForm form = new NotaGeneradorForm();

            if (plantillaId != null) {
                var plantilla = plantillaService.buscarPorId(plantillaId)
                        .filter(p -> p.isActivo())
                        .orElseThrow(()
                                -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "La plantilla no existe o está inactiva."
                                )
                        );

                boolean autorizado = autenticacion != null
                        && autenticacion.getAuthorities().stream()
                                .anyMatch(autoridad
                                        -> autoridad.getAuthority()
                                                .equals("ROLE_ADMIN")
                                        || plantilla.getRoles().stream()
                                                .anyMatch(rol
                                                        -> autoridad.getAuthority()
                                                                .equals(
                                                                        "ROLE_"
                                                                        + rol.getNombre()
                                                                )
                                                )
                                );

                if (!autorizado) {
                    throw new ResponseStatusException(
                            HttpStatus.FORBIDDEN,
                            "Tu perfil no está autorizado "
                            + "para usar esta plantilla."
                    );
                }

                form.setPlantillaId(plantilla.getIdPlantilla());
            }

            model.addAttribute("form", form);
        }

        return "Generador_Notas/generador";
    }

    // Generar vista previa.
    @PostMapping("/generador/preview")
    public String preview(
            @Valid @ModelAttribute("form") NotaGeneradorForm form,
            BindingResult bindingResult,
            Model model) {

        model.addAttribute(
                "plantillas",
                plantillaService.findAllActive()
        );

        model.addAttribute(
                "miembros",
                teamMemberService.findAll()
        );

        if (bindingResult.hasErrors()) {
            return "Generador_Notas/generador";
        }

        model.addAttribute(
                "preview",
                notaCorreccionService.generarVistaPrevia(form)
        );

        return "Generador_Notas/generador";
    }

    // Guardar una nota nueva.
    @PostMapping("/generador/guardar")
    public String guardar(
            @Valid @ModelAttribute("form") NotaGeneradorForm form,
            BindingResult bindingResult,
            Model model) {

        model.addAttribute(
                "plantillas",
                plantillaService.findAllActive()
        );

        model.addAttribute(
                "miembros",
                teamMemberService.findAll()
        );

        if (bindingResult.hasErrors()) {
            return "Generador_Notas/generador";
        }

        try {
            var nota = notaCorreccionService.guardar(form);

            return "redirect:/Generador_Notas/notas/"
                    + nota.getIdNota();

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());

            return "Generador_Notas/generador";
        }
    }

    // Listar notas guardadas.
    @GetMapping("/notas")
    public String listarNotas(Model model) {
        model.addAttribute(
                "notas",
                notaCorreccionService.findAll()
        );

        return "Generador_Notas/lista";
    }

    // Consultar una nota.
    @GetMapping("/notas/{id}")
    public String detalleNota(
            @PathVariable Integer id,
            Model model) {

        model.addAttribute(
                "nota",
                notaCorreccionService.findById(id)
        );

        return "Generador_Notas/detalle";
    }

    // Abrir una nota para editarla.
    @GetMapping("/notas/{id}/editar")
    public String editarNota(
            @PathVariable Integer id,
            Model model) {

        var nota = notaCorreccionService.findById(id);

        NotaGeneradorForm form = new NotaGeneradorForm();

        form.setPlantillaId(
                nota.getPlantilla().getIdPlantilla()
        );

        form.setTeamMemberId(
                nota.getTeamMember().getIdTeamMember()
        );

        form.setFechaInicio(nota.getFechaError());
        form.setFechaFin(nota.getFechaEmision());
        form.setDescripcion(nota.getDescripcionError());
        form.setCorreccion(nota.getCorreccionRealizada());
        form.setEvidencia(nota.getEvidenciaObjetiva());

        model.addAttribute("form", form);

        model.addAttribute(
                "plantillas",
                plantillaService.findAllActive()
        );

        model.addAttribute(
                "miembros",
                teamMemberService.findAll()
        );

        model.addAttribute("notaId", id);

        return "Generador_Notas/generador";
    }

    // Guardar cambios de una nota.
    @PostMapping("/notas/{id}/editar")
    public String actualizarNota(
            @PathVariable Integer id,
            @Valid @ModelAttribute("form") NotaGeneradorForm form,
            BindingResult bindingResult,
            Model model) {

        model.addAttribute(
                "plantillas",
                plantillaService.findAllActive()
        );

        model.addAttribute(
                "miembros",
                teamMemberService.findAll()
        );

        if (bindingResult.hasErrors()) {
            model.addAttribute("notaId", id);

            return "Generador_Notas/generador";
        }

        try {
            notaCorreccionService.actualizar(id, form);

            return "redirect:/Generador_Notas/notas/" + id;

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("notaId", id);

            return "Generador_Notas/generador";
        }
    }

    // Eliminar una nota.
    @PostMapping("/notas/{id}/eliminar")
    public String eliminarNota(@PathVariable Integer id) {
        notaCorreccionService.eliminar(id);

        return "redirect:/Generador_Notas/notas";
    }

    // Redirigir al generador.
    @GetMapping("/")
    public String index() {
        return "redirect:/Generador_Notas/generador";
    }
}
