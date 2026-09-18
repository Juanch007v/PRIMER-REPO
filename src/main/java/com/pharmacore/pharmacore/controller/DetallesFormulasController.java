package com.pharmacore.pharmacore.controller;

import com.pharmacore.pharmacore.model.DetallesFormulas;
import com.pharmacore.pharmacore.repository.DetallesFormulasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalles-formulas")
public class DetallesFormulasController {

    @Autowired
    private DetallesFormulasRepository detallesFormulasRepository;

    @GetMapping
    public List<DetallesFormulas> getAll() {
        return detallesFormulasRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetallesFormulas> getById(@PathVariable Long id) {
        return detallesFormulasRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/formula/{idFormula}")
    public List<DetallesFormulas> porFormula(@PathVariable Long idFormula) {
        return detallesFormulasRepository.findByIdFormula(idFormula);
    }

    @PostMapping
    public DetallesFormulas create(@RequestBody DetallesFormulas detalle) {
        return detallesFormulasRepository.save(detalle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!detallesFormulasRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        detallesFormulasRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
