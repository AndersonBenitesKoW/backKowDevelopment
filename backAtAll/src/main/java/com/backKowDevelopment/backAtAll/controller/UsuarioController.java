package com.backKowDevelopment.backAtAll.controller;

import com.backKowDevelopment.backAtAll.model.Usuario;
import com.backKowDevelopment.backAtAll.model.UsuarioDto;
import com.backKowDevelopment.backAtAll.service.UsuarioService;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData)
            throws ExecutionException, InterruptedException {

        String email = loginData.get("email");
        String password = loginData.get("contraseña");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Faltan email o contraseña"));
        }

        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future = db.collection("usuarios")
                .whereEqualTo("email", email)
                .limit(1)
                .get();

        List<QueryDocumentSnapshot> docs = future.get().getDocuments();

        if (docs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario no encontrado"));
        }

        DocumentSnapshot doc = docs.get(0);
        String stored = doc.getString("contraseña");

        if (stored == null || !stored.equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }

        // Devuelve todo el usuario (con contraseña, solo para pruebas)
        Usuario u = doc.toObject(Usuario.class);
        if (u != null) u.setId(doc.getId());
        return ResponseEntity.ok(u);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Usuario> createUsuario(@RequestBody Usuario usuario)
            throws ExecutionException, InterruptedException {
        String id = usuarioService.createUsuario(usuario);
        Usuario creado = usuarioService.getUsuarioById(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUsuario(@PathVariable String id, @RequestBody Usuario usuario)
            throws ExecutionException, InterruptedException {
        usuarioService.updateUsuario(id, usuario);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchUsuario(@PathVariable String id, @RequestBody Map<String, Object> updates)
            throws ExecutionException, InterruptedException {
        usuarioService.patchUsuario(id, updates);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable String id)
            throws ExecutionException, InterruptedException {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = {"", "/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Usuario>> getAllUsuarios()
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(usuarioService.getAllUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable String id)
            throws ExecutionException, InterruptedException {
        Usuario u = usuarioService.getUsuarioById(id);
        if (u == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(u);
    }

    @GetMapping("/verificar-email")
    public ResponseEntity<Boolean> verificarEmail(@RequestParam String email)
            throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection("usuarios")
                .whereEqualTo("email", email)
                .limit(1)
                .get();
        boolean existe = !future.get().getDocuments().isEmpty();
        return ResponseEntity.ok(existe);
    }




}