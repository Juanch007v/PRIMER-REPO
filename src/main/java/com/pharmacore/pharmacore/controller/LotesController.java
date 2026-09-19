package com.pharmacore.pharmacore.controller;

import com.pharmacore.pharmacore.model.Lotes;
import com.pharmacore.pharmacore.repository.LotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lotes")
public class LotesController {

    @Autowired
    private LotesRepository lotesRepository;

    @GetMapping
    public List<Lotes> getAll() {
        return lotesRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lotes> getById(@PathVariable Integer id) {
        return lotesRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/producto/{idProducto}")
    public List<Lotes> porProducto(@PathVariable Integer idProducto) {
        return lotesRepository.findByIdProductoOrderByFechaVencimientoAsc(idProducto);
    }

    @PostMapping
    public ResponseEntity<Lotes> create(@RequestBody Lotes lote) {
        Lotes guardado = lotesRepository.save(lote);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lotes> update(@PathVariable Integer id, @RequestBody Lotes lote) {
        if (!lotesRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lote.setIdLote(id);
        return ResponseEntity.ok(lotesRepository.save(lote));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!lotesRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lotesRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
