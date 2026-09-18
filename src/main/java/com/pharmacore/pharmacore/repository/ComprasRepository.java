package com.pharmacore.pharmacore.repository;

import com.pharmacore.pharmacore.model.Compras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComprasRepository extends JpaRepository<Compras, Integer> {
}
