package com.backKowDevelopment.backAtAll.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago {
    private String id;
    private Date createdAt;
    private String estado;
    private String metodo;
    private String moneda;
    private double monto;
    private String pedidoId;
    private String proveedorId;
    private Date updatedAt;
}