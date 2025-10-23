package com.backKowDevelopment.backAtAll.controller;

import com.backKowDevelopment.backAtAll.model.Cliente;
import com.backKowDevelopment.backAtAll.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<Cliente>> getAllClientes() throws ExecutionException, InterruptedException {
        List<Cliente> clientes = clienteService.getAllClientes();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable String id) throws ExecutionException, InterruptedException {
        Cliente cliente = clienteService.getClienteById(id);
        if (cliente != null) {
            return ResponseEntity.ok(cliente);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> createCliente(@RequestBody Cliente cliente) throws ExecutionException, InterruptedException {
        String id = clienteService.createCliente(cliente);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCliente(@PathVariable String id, @RequestBody Cliente cliente) throws ExecutionException, InterruptedException {
        clienteService.updateCliente(id, cliente);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable String id) throws ExecutionException, InterruptedException {
        clienteService.deleteCliente(id);
        return ResponseEntity.ok().build();
    }
}