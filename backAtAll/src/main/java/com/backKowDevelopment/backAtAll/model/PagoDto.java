package com.backKowDevelopment.backAtAll.model;

import java.util.Date;

public record PagoDto(
        String id,
        Date createdAt,
        String estado,
        String metodo,
        String moneda,
        double monto,
        String pedidoId,   // <- String en la respuesta
        String proveedorId,
        Date updatedAt
) {}
