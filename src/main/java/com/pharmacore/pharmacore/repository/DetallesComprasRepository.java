package com.pharmacore.pharmacore.repository;

import com.pharmacore.pharmacore.model.DetallesCompras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallesComprasRepository extends JpaRepository<DetallesCompras, Integer> {
    List<DetallesCompras> findByCompra_IdCompra(Integer idCompra);
}
