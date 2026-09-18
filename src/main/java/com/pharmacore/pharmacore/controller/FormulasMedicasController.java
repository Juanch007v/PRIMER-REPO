package com.pharmacore.pharmacore.controller;

import com.pharmacore.pharmacore.model.FormulasMedicas;
import com.pharmacore.pharmacore.repository.FormulasMedicasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formulas-medicas")
public class FormulasMedicasController {

    @Autowired
    private FormulasMedicasRepository formulasRepository;

    @GetMapping
    public List<FormulasMedicas> getAll() {
        return formulasRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormulasMedicas> getById(@PathVariable Long id) {
        return formulasRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{idCliente}")
    public List<FormulasMedicas> porCliente(@PathVariable Long idCliente) {
        return formulasRepository.findByIdCliente(idCliente);
    }

    @PostMapping
    public ResponseEntity<FormulasMedicas> create(@RequestBody FormulasMedicas formula) {
        FormulasMedicas guardada = formulasRepository.save(formula);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!formulasRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        formulasRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
