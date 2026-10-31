package com.smartnotes.service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartnotes.domain.Categoria;
import com.smartnotes.domain.Plantilla;
import com.smartnotes.domain.PlantillaVariable;
import com.smartnotes.domain.Rol;
import com.smartnotes.domain.Usuario;
import com.smartnotes.domain.Variable;
import com.smartnotes.dto.PlantillaFormulario;
import com.smartnotes.repository.CategoriaRepository;
import com.smartnotes.repository.PlantillaRepository;
import com.smartnotes.repository.PlantillaVariableRepository;
import com.smartnotes.repository.RolRepository;
import com.smartnotes.repository.UsuarioRepository;
import com.smartnotes.repository.VariableRepository;

import jakarta.validation.Validator;

@Service
@Transactional(readOnly = true)
public class PlantillaService {

    private final PlantillaRepository plantillaRepository;
    private final CategoriaRepository categoriaRepository;
    private final RolRepository rolRepository;
    private final VariableRepository variableRepository;
    private final PlantillaVariableRepository relacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final Validator validator;

    public PlantillaService(
            PlantillaRepository plantillaRepository,
            CategoriaRepository categoriaRepository,
            RolRepository rolRepository,
            VariableRepository variableRepository,
            PlantillaVariableRepository relacionRepository,
            UsuarioRepository usuarioRepository,
            Validator validator) {

        this.plantillaRepository = plantillaRepository;
        this.categoriaRepository = categoriaRepository;
        this.rolRepository = rolRepository;
        this.variableRepository = variableRepository;
        this.relacionRepository = relacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.validator = validator;
    }

    public List<Plantilla> listarTodas() {
        List<Plantilla> plantillas = plantillaRepository.findAll();

        plantillas.forEach(this::cargarRelaciones);

        return plantillas;
    }

    public Optional<Plantilla> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }

        Optional<Plantilla> resultado
                = plantillaRepository.findById(id);

        resultado.ifPresent(this::cargarRelaciones);

        return resultado;
    }

    public List<Plantilla> buscarActivas(
            String nombre,
            String perfil,
            String categoria) {

        List<Plantilla> resultado = plantillaRepository
                .findByActivoTrue()
                .stream()
                .filter(plantilla
                        -> normalizar(plantilla.getNombre())
                                .contains(normalizar(nombre))
                )
                .filter(plantilla
                        -> normalizar(perfil).isEmpty()
                        || plantilla.getRoles().stream()
                                .anyMatch(rol
                                        -> normalizar(rol.getNombre())
                                                .equals(normalizar(perfil))
                                )
                )
                .filter(plantilla
                        -> normalizar(categoria).isEmpty()
                        || (plantilla.getCategoria() != null
                        && normalizar(
                                plantilla.getCategoria().getNombre()
                        ).equals(normalizar(categoria)))
                )
                .toList();

        resultado.forEach(this::cargarRelaciones);

        return resultado;
    }

    public List<Plantilla> findAllActive() {
        return buscarActivas("", "", "");
    }

    public Plantilla findById(Integer id) {
        return obtenerPlantilla(id);
    }

    @Transactional
    public void cambiarEstado(Integer id, boolean activo) {
        Plantilla plantilla = obtenerPlantilla(id);

        plantilla.setActivo(activo);
        plantillaRepository.save(plantilla);
    }

    public PlantillaFormulario obtenerFormulario(Integer id) {
        Plantilla plantilla = obtenerPlantilla(id);
        cargarRelaciones(plantilla);

        PlantillaFormulario formulario = new PlantillaFormulario();

        formulario.setIdPlantilla(plantilla.getIdPlantilla());
        formulario.setNombre(plantilla.getNombre());
        formulario.setDescripcion(plantilla.getDescripcion());
        formulario.setTipoAsociado(plantilla.getTipoAsociado());
        formulario.setContenido(plantilla.getContenido());
        formulario.setActivo(plantilla.isActivo());
        formulario.setRequiereEvidencia(
                plantilla.isRequiereEvidencia()
        );

        if (plantilla.getCategoria() != null) {
            formulario.setIdCategoria(
                    plantilla.getCategoria().getIdCategoria()
            );
        }

        Set<Integer> idsRoles = new HashSet<>();

        for (Rol rol : plantilla.getRoles()) {
            idsRoles.add(rol.getIdRol());
        }

        formulario.setIdsRoles(idsRoles);

        List<PlantillaFormulario.VariableFormulario> variables
                = new ArrayList<>();

        for (PlantillaVariable relacion : plantilla.getVariables()) {
            Variable variable = relacion.getVariable();

            PlantillaFormulario.VariableFormulario campo
                    = new PlantillaFormulario.VariableFormulario();

            campo.setNombre(variable.getNombre());
            campo.setDescripcion(variable.getDescripcion());
            campo.setObligatoria(relacion.isObligatoria());

            variables.add(campo);
        }

        formulario.setVariables(variables);

        return formulario;
    }

    @Transactional
    public Plantilla guardar(
            PlantillaFormulario formulario,
            String username) {

        validarFormulario(formulario);

        Categoria categoria = categoriaRepository
                .findById(formulario.getIdCategoria())
                .orElseThrow(()
                        -> new IllegalArgumentException(
                                "La categoría seleccionada no existe."
                        )
                );

        if (!categoria.isActivo()) {
            throw new IllegalArgumentException(
                    "Selecciona una categoría activa."
            );
        }

        Set<Rol> roles = new HashSet<>(
                rolRepository.findAllById(formulario.getIdsRoles())
        );

        if (roles.size() != formulario.getIdsRoles().size()) {
            throw new IllegalArgumentException(
                    "Uno de los perfiles seleccionados no existe."
            );
        }

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(()
                        -> new IllegalArgumentException(
                                "El usuario responsable no existe."
                        )
                );

        String nombre = formulario.getNombre().trim();

        boolean nombreRepetido = plantillaRepository.findAll()
                .stream()
                .anyMatch(plantilla
                        -> !Objects.equals(
                                plantilla.getIdPlantilla(),
                                formulario.getIdPlantilla()
                        )
                        && normalizar(plantilla.getNombre())
                                .equals(normalizar(nombre))
                );

        if (nombreRepetido) {
            throw new IllegalArgumentException(
                    "Ya existe otra plantilla con ese nombre."
            );
        }

        Plantilla plantilla = formulario.getIdPlantilla() == null
                ? new Plantilla()
                : obtenerPlantilla(formulario.getIdPlantilla());

        plantilla.setNombre(nombre);
        plantilla.setDescripcion(formulario.getDescripcion().trim());
        plantilla.setTipoAsociado(
                formulario.getTipoAsociado() == null
                ? ""
                : formulario.getTipoAsociado().trim()
        );
        plantilla.setContenido(formulario.getContenido().trim());
        plantilla.setCategoria(categoria);
        plantilla.setRoles(roles);
        plantilla.setActivo(formulario.isActivo());
        plantilla.setRequiereEvidencia(
                formulario.isRequiereEvidencia()
        );
        plantilla.setUsuarioModificacion(usuario);

        plantilla = plantillaRepository.saveAndFlush(plantilla);

        // Reemplazar las relaciones de esta plantilla.
        List<PlantillaVariable> anteriores
                = relacionRepository
                        .findByPlantilla_IdPlantillaOrderByOrdenAsc(
                                plantilla.getIdPlantilla()
                        );

        plantilla.getVariables().clear();
        relacionRepository.deleteAll(anteriores);
        relacionRepository.flush();

        List<PlantillaVariable> nuevas = new ArrayList<>();
        int orden = 0;

        for (PlantillaFormulario.VariableFormulario campo
                : formulario.getVariables()) {

            Variable variable = obtenerOCrearVariable(campo);

            PlantillaVariable relacion = new PlantillaVariable();

            relacion.setPlantilla(plantilla);
            relacion.setVariable(variable);
            relacion.setObligatoria(campo.isObligatoria());
            relacion.setOrden(orden++);

            nuevas.add(relacion);
        }

        relacionRepository.saveAll(nuevas);
        plantilla.getVariables().addAll(nuevas);

        return plantilla;
    }

    private void validarFormulario(PlantillaFormulario formulario) {
        var errores = validator.validate(formulario);

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(
                    errores.iterator().next().getMessage()
            );
        }

        Set<String> nombresDefinidos = new HashSet<>();
        String contenido = formulario.getContenido();

        // Comprobar las definiciones y las variables obligatorias.
        for (PlantillaFormulario.VariableFormulario campo
                : formulario.getVariables()) {

            String nombre = campo.getNombre().trim();

            if (!nombresDefinidos.add(nombre)) {
                throw new IllegalArgumentException(
                        "La variable [" + nombre + "] está repetida."
                );
            }

            if (campo.isObligatoria()
                    && !contenido.contains("[" + nombre + "]")) {

                throw new IllegalArgumentException(
                        "Falta la variable obligatoria ["
                        + nombre + "] en el contenido."
                );
            }
        }

        // Encontrar las variables escritas entre corchetes.
        var patron = java.util.regex.Pattern.compile(
                "\\[([^\\[\\]]+)\\]"
        );

        var coincidencias = patron.matcher(contenido);
        Set<String> sinDefinir = new java.util.LinkedHashSet<>();

        while (coincidencias.find()) {
            String nombre = coincidencias.group(1);

            if (!nombresDefinidos.contains(nombre)) {
                sinDefinir.add("[" + nombre + "]");
            }
        }

        if (!sinDefinir.isEmpty()) {
            throw new IllegalArgumentException(
                    "Estas variables aparecen en el contenido, "
                    + "pero no están definidas en la tabla: "
                    + String.join(", ", sinDefinir)
            );
        }
    }

    private Variable obtenerOCrearVariable(
            PlantillaFormulario.VariableFormulario campo) {

        String nombre = campo.getNombre().trim();
        String descripcion = campo.getDescripcion().trim();

        Optional<Variable> existente
                = variableRepository.findByNombre(nombre);

        if (existente.isPresent()) {
            Variable variable = existente.get();

            String descripcionActual = variable.getDescripcion() == null
                    ? ""
                    : variable.getDescripcion().trim();

            if (!descripcionActual.equals(descripcion)) {
                throw new IllegalArgumentException(
                        "La variable [" + nombre + "] ya existe. "
                        + "Utiliza su descripción actual: "
                        + descripcionActual
                );
            }

            return variable;
        }

        Variable variable = new Variable();

        variable.setNombre(nombre);
        variable.setDescripcion(descripcion);
        variable.setTipoDato("TEXTO");

        return variableRepository.saveAndFlush(variable);
    }

    private Plantilla obtenerPlantilla(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "El identificador de la plantilla es obligatorio."
            );
        }

        return plantillaRepository.findById(id)
                .orElseThrow(()
                        -> new IllegalArgumentException(
                                "Plantilla no encontrada."
                        )
                );
    }

    private void cargarRelaciones(Plantilla plantilla) {
        if (plantilla.getCategoria() != null) {
            plantilla.getCategoria().getNombre();
        }

        plantilla.getRoles().forEach(rol -> rol.getNombre());

        plantilla.getVariables().forEach(relacion -> {
            if (relacion.getVariable() != null) {
                relacion.getVariable().getNombre();
                relacion.getVariable().getDescripcion();
            }
        });
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }

        return Normalizer.normalize(
                texto.trim(),
                Normalizer.Form.NFD
        )
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }

    public List<Plantilla> findAll() {
        return listarTodas();
    }
}
