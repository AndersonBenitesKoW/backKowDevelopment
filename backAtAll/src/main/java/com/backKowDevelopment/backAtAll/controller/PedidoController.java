package com.backKowDevelopment.backAtAll.controller;

import com.backKowDevelopment.backAtAll.model.Pedido;
import com.backKowDevelopment.backAtAll.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<Pedido>> getAllPedidos() throws ExecutionException, InterruptedException {
        List<Pedido> pedidos = pedidoService.getAllPedidos();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedidoById(@PathVariable String id) throws ExecutionException, InterruptedException {
        Pedido pedido = pedidoService.getPedidoById(id);
        if (pedido != null) {
            return ResponseEntity.ok(pedido);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> createPedido(@RequestBody Pedido pedido) throws ExecutionException, InterruptedException {
        String id = pedidoService.createPedido(pedido);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePedido(@PathVariable String id, @RequestBody Pedido pedido) throws ExecutionException, InterruptedException {
        pedidoService.updatePedido(id, pedido);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable String id) throws ExecutionException, InterruptedException {
        pedidoService.deletePedido(id);
        return ResponseEntity.ok().build();
    }
}