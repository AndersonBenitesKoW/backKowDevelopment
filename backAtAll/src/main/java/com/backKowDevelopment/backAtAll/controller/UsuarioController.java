package com.backKowDevelopment.backAtAll.controller;

import com.backKowDevelopment.backAtAll.model.Usuario;
import com.backKowDevelopment.backAtAll.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() throws ExecutionException, InterruptedException {
        List<Usuario> usuarios = usuarioService.getAllUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable String id) throws ExecutionException, InterruptedException {
        Usuario usuario = usuarioService.getUsuarioById(id);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> createUsuario(@RequestBody Usuario usuario) throws ExecutionException, InterruptedException {
        String id = usuarioService.createUsuario(usuario);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUsuario(@PathVariable String id, @RequestBody Usuario usuario) throws ExecutionException, InterruptedException {
        usuarioService.updateUsuario(id, usuario);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable String id) throws ExecutionException, InterruptedException {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.ok().build();
    }
}