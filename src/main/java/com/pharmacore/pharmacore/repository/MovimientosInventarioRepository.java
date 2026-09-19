package com.pharmacore.pharmacore.repository;

import com.pharmacore.pharmacore.model.MovimientosInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientosInventarioRepository extends JpaRepository<MovimientosInventario, Integer> {

    List<MovimientosInventario> findByIdProductoOrderByFechaMovimientoDesc(Integer idProducto);

    List<MovimientosInventario> findAllByOrderByFechaMovimientoDesc();
}
