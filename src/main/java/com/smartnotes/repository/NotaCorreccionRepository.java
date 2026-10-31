package com.smartnotes.repository;

import com.smartnotes.domain.NotaCorreccion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotaCorreccionRepository extends JpaRepository<NotaCorreccion, Integer> {

    List<NotaCorreccion> findAllByOrderByFechaCreacionDesc();
}
