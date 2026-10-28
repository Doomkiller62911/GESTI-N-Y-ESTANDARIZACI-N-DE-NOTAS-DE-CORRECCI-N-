package com.smartnotes.repository;

import com.smartnotes.domain.Variable;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariableRepository
        extends JpaRepository<Variable, Integer> {

    Optional<Variable> findByNombre(String nombre);
}