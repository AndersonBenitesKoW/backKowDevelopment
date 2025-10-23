package com.backKowDevelopment.backAtAll.controller;

import com.backKowDevelopment.backAtAll.model.Categoria;
import com.backKowDevelopment.backAtAll.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<Categoria>> getAllCategorias() throws ExecutionException, InterruptedException {
        List<Categoria> categorias = categoriaService.getAllCategorias();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getCategoriaById(@PathVariable String id) throws ExecutionException, InterruptedException {
        Categoria categoria = categoriaService.getCategoriaById(id);
        if (categoria != null) {
            return ResponseEntity.ok(categoria);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> createCategoria(@RequestBody Categoria categoria) throws ExecutionException, InterruptedException {
        String id = categoriaService.createCategoria(categoria);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCategoria(@PathVariable String id, @RequestBody Categoria categoria) throws ExecutionException, InterruptedException {
        categoriaService.updateCategoria(id, categoria);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable String id) throws ExecutionException, InterruptedException {
        categoriaService.deleteCategoria(id);
        return ResponseEntity.ok().build();
    }
}