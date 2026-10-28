package com.smartnotes.repository;

import com.smartnotes.domain.NotaCorreccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotaCorreccionRepository extends JpaRepository<NotaCorreccion, Long> {

    List<NotaCorreccion> findAllByOrderByFechaCreacionDesc();
}
