package com.smartnotes.repository;

import com.smartnotes.domain.Plantilla;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantillaRepository extends JpaRepository<Plantilla, Integer> {

    public List<Plantilla> findByActivoTrue();

}
