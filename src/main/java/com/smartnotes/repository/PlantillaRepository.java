package com.smartnotes.repository;

import com.smartnotes.domain.Plantilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlantillaRepository extends JpaRepository<Plantilla, Long> {

    List<Plantilla> findByActivoTrueOrderByNombreAsc();
}
