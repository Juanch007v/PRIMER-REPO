package com.pharmacore.pharmacore.controller;

import com.pharmacore.pharmacore.model.DetallesVentas;
import com.pharmacore.pharmacore.repository.DetallesVentasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalles-ventas")
public class DetallesVentasController {

    @Autowired
    private DetallesVentasRepository detallesVentasRepository;

    @GetMapping
    public List<DetallesVentas> listarTodos() {
        return detallesVentasRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetallesVentas> obtenerPorId(@PathVariable Long id) {
        return detallesVentasRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/venta/{idVenta}")
    public List<DetallesVentas> listarPorVenta(@PathVariable Long idVenta) {
        return detallesVentasRepository.findByVenta_IdVenta(idVenta);
    }

    @PostMapping
    public DetallesVentas guardarDetalle(@RequestBody DetallesVentas detalleVenta) {
        return detallesVentasRepository.save(detalleVenta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable Long id) {
        if (detallesVentasRepository.existsById(id)) {
            detallesVentasRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
