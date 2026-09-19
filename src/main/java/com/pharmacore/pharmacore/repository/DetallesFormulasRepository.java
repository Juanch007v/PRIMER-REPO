package com.pharmacore.pharmacore.repository;

import com.pharmacore.pharmacore.model.DetallesFormulas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallesFormulasRepository extends JpaRepository<DetallesFormulas, Long> {
    List<DetallesFormulas> findByIdFormula(Long idFormula);
}
