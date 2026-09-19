package com.pharmacore.pharmacore.repository;

import com.pharmacore.pharmacore.model.DetallesVentas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallesVentasRepository extends JpaRepository<DetallesVentas, Long> {

    // Líneas de productos asociadas a una venta específica
    List<DetallesVentas> findByVenta_IdVenta(Long idVenta);
}
