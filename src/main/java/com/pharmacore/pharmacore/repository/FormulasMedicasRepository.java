package com.pharmacore.pharmacore.repository;

import com.pharmacore.pharmacore.model.FormulasMedicas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormulasMedicasRepository extends JpaRepository<FormulasMedicas, Long> {
    List<FormulasMedicas> findByIdCliente(Long idCliente);
}
