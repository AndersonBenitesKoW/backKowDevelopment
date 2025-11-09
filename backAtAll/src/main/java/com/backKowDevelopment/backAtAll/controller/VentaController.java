package com.backKowDevelopment.backAtAll.controller;

import com.backKowDevelopment.backAtAll.model.CreateResponse;
import com.backKowDevelopment.backAtAll.model.Venta;
import com.backKowDevelopment.backAtAll.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @GetMapping
    public ResponseEntity<List<Venta>> getAllVentas() throws ExecutionException, InterruptedException {
        List<Venta> ventas = ventaService.getAllVentas();
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> getVentaById(@PathVariable String id) throws ExecutionException, InterruptedException {
        Venta venta = ventaService.getVentaById(id);
        if (venta != null) {
            return ResponseEntity.ok(venta);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<CreateResponse> createVenta(@RequestBody Venta venta)
            throws ExecutionException, InterruptedException {

        String id = ventaService.createVenta(venta);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateResponse(id)); // ✅ devuelve { "id": "XUzNw1ookVwU2pnXf0dg" }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateVenta(@PathVariable String id, @RequestBody Venta venta) throws ExecutionException, InterruptedException {
        ventaService.updateVenta(id, venta);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenta(@PathVariable String id) throws ExecutionException, InterruptedException {
        ventaService.deleteVenta(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchVenta(
            @PathVariable String id,
            @RequestBody Map<String, Object> updates) throws ExecutionException, InterruptedException {
        ventaService.patchVenta(id, updates);
        return ResponseEntity.ok().build();
    }
}