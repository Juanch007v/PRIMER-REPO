package com.pharmacore.pharmacore.controller;

import com.pharmacore.pharmacore.model.Ventas;
import com.pharmacore.pharmacore.repository.VentasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentasController {

    @Autowired
    private VentasRepository ventasRepository;

    @GetMapping
    public List<Ventas> getAll() {
        return ventasRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ventas> getById(@PathVariable Long id) {
        return ventasRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Ventas> create(@RequestBody Ventas venta) {
        Ventas guardada = ventasRepository.save(venta);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!ventasRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        ventasRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
