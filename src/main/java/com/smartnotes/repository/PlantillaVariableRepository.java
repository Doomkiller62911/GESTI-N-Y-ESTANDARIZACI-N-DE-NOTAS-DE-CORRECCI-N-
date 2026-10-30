package com.smartnotes.repository;

import com.smartnotes.domain.PlantillaVariable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantillaVariableRepository
        extends JpaRepository<PlantillaVariable, Integer> {

    List<PlantillaVariable>
            findByPlantilla_IdPlantillaOrderByOrdenAsc(Integer idPlantilla);
}