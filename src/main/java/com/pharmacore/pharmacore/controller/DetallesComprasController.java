package com.pharmacore.pharmacore.controller;

import com.pharmacore.pharmacore.model.DetallesCompras;
import com.pharmacore.pharmacore.repository.DetallesComprasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalles-compras")
public class DetallesComprasController {

    @Autowired
    private DetallesComprasRepository detallesComprasRepository;

    @GetMapping
    public List<DetallesCompras> getAll() {
        return detallesComprasRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetallesCompras> getById(@PathVariable Integer id) {
        return detallesComprasRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/compra/{idCompra}")
    public List<DetallesCompras> porCompra(@PathVariable Integer idCompra) {
        return detallesComprasRepository.findByCompra_IdCompra(idCompra);
    }
}
