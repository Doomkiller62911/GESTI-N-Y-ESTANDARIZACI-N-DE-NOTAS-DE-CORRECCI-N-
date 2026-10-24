package com.smartnotes.service;

import com.smartnotes.domain.Plantilla;
import com.smartnotes.domain.Plantilla.Variable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class PlantillaService {

    // Datos temporales. Se reemplazarán por un repositorio.
    private final List<Plantilla> plantillas = new ArrayList<>();

    public PlantillaService() {
        Plantilla documento = new Plantilla();

        documento.setId(1L);
        documento.setNombre("TM no ingresa documento");
        documento.setDescripcion(
                "Se utiliza cuando un colaborador omite registrar un documento."
        );
        documento.setTipoAsociado("Ingreso/Eliminación de Documento");
        documento.setCategoria("Documentos");
        documento.setPerfil("Supervisor");
        documento.setActiva(true);
        documento.setContenido(
                "[TM], el día [FECHA ERROR], omitió ingresar el documento "
                + "[DOCUMENTO]. El ingreso se realizó el día "
                + "[FECHA DE INGRESO]."
        );

        documento.setVariables(List.of(
                new Variable(
                        "TM",
                        "Nombre del Team Member involucrado",
                        true
                ),
                new Variable(
                        "FECHA ERROR",
                        "Fecha del incidente",
                        true
                ),
                new Variable(
                        "DOCUMENTO",
                        "Código o nombre del documento",
                        true
                ),
                new Variable(
                        "FECHA DE INGRESO",
                        "Fecha de regularización",
                        true
                )
        ));

        Plantilla equipo = new Plantilla();

        equipo.setId(2L);
        equipo.setNombre("TM no ingresa equipo");
        equipo.setDescripcion(
                "Se utiliza cuando un colaborador omite registrar un equipo."
        );
        equipo.setTipoAsociado("Ingreso/Eliminación de Equipo");
        equipo.setCategoria("Equipos");
        equipo.setPerfil("Líder");
        equipo.setActiva(false);
        equipo.setContenido(
                "[TM], el día [FECHA ERROR], omitió registrar el equipo "
                + "[EQUIPO]. Corrección realizada: [CORRECCION]."
        );

        equipo.setVariables(List.of(
                new Variable(
                        "TM",
                        "Nombre del Team Member involucrado",
                        true
                ),
                new Variable(
                        "FECHA ERROR",
                        "Fecha del incidente",
                        true
                ),
                new Variable(
                        "EQUIPO",
                        "Código o nombre del equipo",
                        true
                ),
                new Variable(
                        "CORRECCION",
                        "Descripción de la corrección realizada",
                        true
                )
        ));

        plantillas.add(documento);
        plantillas.add(equipo);
    }

    public List<Plantilla> listarTodas() {
        return List.copyOf(plantillas);
    }

    public Optional<Plantilla> buscarPorId(Long id) {
        return plantillas.stream()
                .filter(plantilla -> plantilla.getId().equals(id))
                .findFirst();
    }

    public List<Plantilla> buscarActivas(
            String nombre,
            String perfil,
            String categoria) {

        return plantillas.stream()
                .filter(Plantilla::isActiva)
                .filter(plantilla
                        -> normalizar(plantilla.getNombre())
                                .contains(normalizar(nombre))
                )
                .filter(plantilla
                        -> normalizar(perfil).isEmpty()
                        || normalizar(plantilla.getPerfil())
                                .equals(normalizar(perfil))
                )
                .filter(plantilla
                        -> normalizar(categoria).isEmpty()
                        || normalizar(plantilla.getCategoria())
                                .equals(normalizar(categoria))
                )
                .toList();
    }

    public void cambiarEstado(Long id, boolean activa) {
        Plantilla plantilla = buscarPorId(id)
                .orElseThrow(()
                        -> new IllegalArgumentException("Plantilla no encontrada")
                );

        plantilla.setActiva(activa);
    }

    public synchronized Plantilla guardar(Plantilla datos) {
        Plantilla destino;

        if (datos.getId() == null) {
            // Crear una plantilla con un identificador nuevo.
            long nuevoId = plantillas.stream()
                    .mapToLong(plantilla -> plantilla.getId())
                    .max()
                    .orElse(0L) + 1L;

            destino = new Plantilla();
            destino.setId(nuevoId);
        } else {
            // Actualizar únicamente si la plantilla existe.
            destino = buscarPorId(datos.getId())
                    .orElseThrow(()
                            -> new IllegalArgumentException(
                                    "La plantilla que intentas editar no existe."
                            )
                    );
        }

        // Copiar las variables para no compartir la lista del formulario.
        List<Variable> variables = new ArrayList<>();

        for (Variable variable : datos.getVariables()) {
            variables.add(new Variable(
                    variable.getNombre().trim(),
                    variable.getDescripcion().trim(),
                    variable.isObligatoria()
            ));
        }

        destino.setNombre(datos.getNombre().trim());
        destino.setDescripcion(datos.getDescripcion().trim());

        destino.setTipoAsociado(
                datos.getTipoAsociado() == null
                ? ""
                : datos.getTipoAsociado().trim()
        );

        destino.setCategoria(datos.getCategoria());
        destino.setPerfil(datos.getPerfil());
        destino.setContenido(datos.getContenido().trim());
        destino.setActiva(datos.isActiva());
        destino.setVariables(variables);

        if (datos.getId() == null) {
            plantillas.add(destino);
        }

        return destino;
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }

        return Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
