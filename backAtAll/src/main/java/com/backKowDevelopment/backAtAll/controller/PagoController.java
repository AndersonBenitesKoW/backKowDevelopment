package com.backKowDevelopment.backAtAll.controller;

import com.backKowDevelopment.backAtAll.model.Pago;
import com.backKowDevelopment.backAtAll.model.PagoCreateDto;
import com.backKowDevelopment.backAtAll.model.PagoDto;
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

    private PagoDto toDto(Pago p) {
        return new PagoDto(
                p.getId(),
                p.getCreatedAt(),
                p.getEstado(),
                p.getMetodo(),
                p.getMoneda(),
                p.getMonto(),
                p.getPedidoId() != null ? p.getPedidoId().getId() : null,
                p.getProveedorId(),
                p.getUpdatedAt()
        );
    }

    @GetMapping
    public ResponseEntity<List<PagoDto>> getAllPagos() throws ExecutionException, InterruptedException {
        List<Pago> pagos = pagoService.getAllPagos();
        var out = pagos.stream().map(this::toDto).toList();
        return ResponseEntity.ok(out);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoDto> getPagoById(@PathVariable String id) throws ExecutionException, InterruptedException {
        Pago p = pagoService.getPagoById(id);
        if (p == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(p));
    }

    @PostMapping
    public ResponseEntity<String> createPago(@RequestBody PagoCreateDto body) throws ExecutionException, InterruptedException {
        String id = pagoService.createPago(body);
        return ResponseEntity.ok(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchPago(@PathVariable String id, @RequestBody PagoCreateDto body)
            throws ExecutionException, InterruptedException {
        pagoService.updatePago(id, body); // merge
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePago(@PathVariable String id, @RequestBody PagoCreateDto body)
            throws ExecutionException, InterruptedException {
        pagoService.updatePago(id, body); // también merge; si prefieres reemplazo total, cambia la lógica del service
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePago(@PathVariable String id) throws ExecutionException, InterruptedException {
        pagoService.deletePago(id);
        return ResponseEntity.ok().build();
    }
}
