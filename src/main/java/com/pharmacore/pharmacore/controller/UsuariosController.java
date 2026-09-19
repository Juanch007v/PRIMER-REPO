package com.pharmacore.pharmacore.controller;

import com.pharmacore.pharmacore.model.Usuarios;
import com.pharmacore.pharmacore.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosController {

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<Usuarios> getAll() {
        return usuariosRepository.findAll();
    }

    @GetMapping("/{id}")
    public Usuarios getById(@PathVariable Long id) {
        return usuariosRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Usuarios create(@RequestBody Usuarios usuario) {
        if (usuario.getPasswordHash() == null || usuario.getPasswordHash().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar una contraseña para el nuevo usuario.");
        }
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));
        return usuariosRepository.save(usuario);
    }

    @PutMapping("/{id}")
    public Usuarios update(@PathVariable Long id, @RequestBody Usuarios usuario) {
        usuario.setIdUsuario(id);

        if (usuario.getPasswordHash() == null || usuario.getPasswordHash().isEmpty()) {
            Usuarios existente = usuariosRepository.findById(id).orElse(null);
            if (existente != null) {
                usuario.setPasswordHash(existente.getPasswordHash());
            }
        } else {
            usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));
        }

        return usuariosRepository.save(usuario);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        usuariosRepository.deleteById(id);
    }
}