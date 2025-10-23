package com.backKowDevelopment.backAtAll.controller;

import com.backKowDevelopment.backAtAll.model.Pago;
import com.backKowDevelopment.backAtAll.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @GetMapping
    public ResponseEntity<List<Pago>> getAllPagos() throws ExecutionException, InterruptedException {
        List<Pago> pagos = pagoService.getAllPagos();
        return ResponseEntity.ok(pagos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pago> getPagoById(@PathVariable String id) throws ExecutionException, InterruptedException {
        Pago pago = pagoService.getPagoById(id);
        if (pago != null) {
            return ResponseEntity.ok(pago);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> createPago(@RequestBody Pago pago) throws ExecutionException, InterruptedException {
        String id = pagoService.createPago(pago);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePago(@PathVariable String id, @RequestBody Pago pago) throws ExecutionException, InterruptedException {
        pagoService.updatePago(id, pago);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePago(@PathVariable String id) throws ExecutionException, InterruptedException {
        pagoService.deletePago(id);
        return ResponseEntity.ok().build();
    }
}