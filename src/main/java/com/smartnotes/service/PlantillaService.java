package com.smartnotes.service;

import com.smartnotes.domain.Plantilla;
import com.smartnotes.repository.PlantillaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlantillaService {

    private final PlantillaRepository plantillaRepository;

    public PlantillaService(PlantillaRepository plantillaRepository) {
        this.plantillaRepository = plantillaRepository;
    }

    public List<Plantilla> findAllActive() {
        return plantillaRepository.findByActivoTrueOrderByNombreAsc();
    }

    public Plantilla findById(Long id) {
        return plantillaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plantilla no encontrada: " + id));
    }
}
