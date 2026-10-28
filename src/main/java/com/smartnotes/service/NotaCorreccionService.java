package com.smartnotes.service;

import com.smartnotes.domain.NotaCorreccion;
import com.smartnotes.domain.Plantilla;
import com.smartnotes.domain.TeamMember;
import com.smartnotes.domain.Variable;
import com.smartnotes.dto.NotaGeneradorForm;
import com.smartnotes.repository.NotaCorreccionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotaCorreccionService {

    private final NotaCorreccionRepository notaCorreccionRepository;
    private final PlantillaService plantillaService;
    private final TeamMemberService teamMemberService;

    public NotaCorreccionService(
            NotaCorreccionRepository notaCorreccionRepository,
            PlantillaService plantillaService,
            TeamMemberService teamMemberService) {
        this.notaCorreccionRepository = notaCorreccionRepository;
        this.plantillaService = plantillaService;
        this.teamMemberService = teamMemberService;
    }

    public List<NotaCorreccion> findAll() {
        return notaCorreccionRepository.findAllByOrderByFechaCreacionDesc();
    }

    public NotaCorreccion findById(Long id) {
        return notaCorreccionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nota no encontrada: " + id));
    }

    public String generarVistaPrevia(NotaGeneradorForm form) {
        Plantilla plantilla = plantillaService.findById(form.getPlantillaId());
        TeamMember teamMember = teamMemberService.findById(form.getTeamMemberId());
        return construirContenido(plantilla, teamMember, form);
    }

    public List<String> validarNota(NotaGeneradorForm form) {
        List<String> errores = new ArrayList<>();

        if (form.getFechaInicio() == null) {
            errores.add("La fecha de inicio es obligatoria.");
        }
        if (form.getFechaFin() == null) {
            errores.add("La fecha final es obligatoria.");
        }
        if (form.getFechaInicio() != null && form.getFechaFin() != null && form.getFechaFin().isBefore(form.getFechaInicio())) {
            errores.add("La fecha final no puede ser anterior a la fecha de inicio.");
        }
        if (form.getDescripcion() == null || form.getDescripcion().isBlank()) {
            errores.add("La descripción no puede estar vacía.");
        }
        if (form.getCorreccion() == null || form.getCorreccion().isBlank()) {
            errores.add("La corrección no puede estar vacía.");
        }
        if (form.getEvidencia() != null && form.getEvidencia().isBlank()) {
            errores.add("La evidencia no puede quedar en blanco.");
        }

        return errores;
    }

    @Transactional
    public NotaCorreccion guardar(NotaGeneradorForm form) {
        List<String> errores = validarNota(form);
        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errores));
        }

        Plantilla plantilla = plantillaService.findById(form.getPlantillaId());
        TeamMember teamMember = teamMemberService.findById(form.getTeamMemberId());

        NotaCorreccion nota = new NotaCorreccion();
        nota.setPlantilla(plantilla);
        nota.setTeamMember(teamMember);
        nota.setFechaInicio(form.getFechaInicio());
        nota.setFechaFin(form.getFechaFin());
        nota.setDescripcion(form.getDescripcion());
        nota.setCorreccion(form.getCorreccion());
        nota.setEvidencias(normalizarEvidencias(form.getEvidencia()));
        nota.setEstado(form.isGuardarComoBorrador() ? "BORRADOR" : "GUARDADA");
        nota.setContenidoGenerado(construirContenido(plantilla, teamMember, form));

        return notaCorreccionRepository.save(nota);
    }

    @Transactional
    public NotaCorreccion actualizar(Long id, NotaGeneradorForm form) {
        NotaCorreccion nota = findById(id);
        List<String> errores = validarNota(form);
        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errores));
        }

        Plantilla plantilla = plantillaService.findById(form.getPlantillaId());
        TeamMember teamMember = teamMemberService.findById(form.getTeamMemberId());

        nota.setPlantilla(plantilla);
        nota.setTeamMember(teamMember);
        nota.setFechaInicio(form.getFechaInicio());
        nota.setFechaFin(form.getFechaFin());
        nota.setDescripcion(form.getDescripcion());
        nota.setCorreccion(form.getCorreccion());
        nota.setEvidencias(normalizarEvidencias(form.getEvidencia()));
        nota.setContenidoGenerado(construirContenido(plantilla, teamMember, form));
        nota.setEstado(form.isGuardarComoBorrador() ? "BORRADOR" : "GUARDADA");

        return notaCorreccionRepository.save(nota);
    }

    @Transactional
    public void eliminar(Long id) {
        notaCorreccionRepository.deleteById(id);
    }

    private String construirContenido(Plantilla plantilla, TeamMember teamMember, NotaGeneradorForm form) {
        String template = plantilla.getTextoBase();
        Map<String, String> valores = new HashMap<>();

        valores.put("{{plantilla}}", plantilla.getNombre());
        valores.put("{{teamMember}}", teamMember.getNombre());
        valores.put("{{cargo}}", teamMember.getCargo());
        valores.put("{{fechaInicio}}", form.getFechaInicio() != null ? formatDate(form.getFechaInicio()) : "");
        valores.put("{{fechaFin}}", form.getFechaFin() != null ? formatDate(form.getFechaFin()) : "");
        valores.put("{{descripcion}}", form.getDescripcion() == null ? "" : form.getDescripcion());
        valores.put("{{correccion}}", form.getCorreccion() == null ? "" : form.getCorreccion());
        valores.put("{{evidencias}}", normalizarEvidencias(form.getEvidencia()));

        for (Variable variable : plantilla.getVariables()) {
            String clave = variable.getClave();
            if (clave != null && !clave.isBlank()) {
                String token = "{{" + clave + "}}";
                valores.put(token, valorVariableGenerado(variable, form, teamMember));
            }
        }

        String contenido = template;
        for (Map.Entry<String, String> entrada : valores.entrySet()) {
            contenido = contenido.replace(entrada.getKey(), entrada.getValue() == null ? "" : entrada.getValue());
        }
        return contenido;
    }

    private String valorVariableGenerado(Variable variable, NotaGeneradorForm form, TeamMember teamMember) {
        String clave = variable.getClave();
        if ("teamMember".equalsIgnoreCase(clave) || "member".equalsIgnoreCase(clave)) {
            return teamMember.getNombre();
        }
        if ("cargo".equalsIgnoreCase(clave)) {
            return teamMember.getCargo();
        }
        if ("fechaInicio".equalsIgnoreCase(clave)) {
            return form.getFechaInicio() != null ? formatDate(form.getFechaInicio()) : "";
        }
        if ("fechaFin".equalsIgnoreCase(clave)) {
            return form.getFechaFin() != null ? formatDate(form.getFechaFin()) : "";
        }
        if ("descripcion".equalsIgnoreCase(clave)) {
            return form.getDescripcion() == null ? "" : form.getDescripcion();
        }
        if ("correccion".equalsIgnoreCase(clave)) {
            return form.getCorreccion() == null ? "" : form.getCorreccion();
        }
        if ("evidencias".equalsIgnoreCase(clave)) {
            return normalizarEvidencias(form.getEvidencia());
        }
        return "";
    }

    private String normalizarEvidencias(String evidencia) {
        if (evidencia == null || evidencia.isBlank()) {
            return "Sin evidencias adjuntas";
        }

        String[] items = evidencia.split("[\\r\\n,;]+");
        List<String> evidencias = new ArrayList<>();
        for (String item : items) {
            String valor = item.trim();
            if (!valor.isEmpty()) {
                evidencias.add(valor);
            }
        }

        return evidencias.isEmpty() ? "Sin evidencias adjuntas" : String.join("; ", evidencias);
    }

    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
