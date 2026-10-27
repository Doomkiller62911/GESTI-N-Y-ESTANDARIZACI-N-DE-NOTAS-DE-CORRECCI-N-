package com.smartnotes.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "plantilla_variable")
public class PlantillaVariable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plantilla_variable")
    private Integer idPlantillaVariable;

    @ManyToOne
    @JoinColumn(name = "id_plantilla", nullable = false)
    private Plantilla plantilla;

    @ManyToOne
    @JoinColumn(name = "id_variable", nullable = false)
    private Variable variable;

    @Column(nullable = false)
    private boolean obligatoria = true;

    @Column(nullable = false)
    private int orden = 0;
}
