package com.pharmacore.pharmacore.controller;

import com.pharmacore.pharmacore.model.Compras;
import com.pharmacore.pharmacore.repository.ComprasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
public class ComprasController {

    @Autowired
    private ComprasRepository comprasRepository;

    @GetMapping
    public List<Compras> getAll() {
        return comprasRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Compras> getById(@PathVariable Integer id) {
        return comprasRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Compras> create(@RequestBody Compras compra) {
        Compras guardada = comprasRepository.save(compra);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Compras> update(@PathVariable Integer id, @RequestBody Compras compra) {
        if (!comprasRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        compra.setIdCompra(id);
        return ResponseEntity.ok(comprasRepository.save(compra));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!comprasRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        comprasRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
