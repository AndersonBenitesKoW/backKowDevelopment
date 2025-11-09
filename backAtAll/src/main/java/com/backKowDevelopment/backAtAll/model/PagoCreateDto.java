package com.backKowDevelopment.backAtAll.model;
import lombok.Data;
@Data
public class PagoCreateDto {
    private String estado;
    private String metodo;
    private String moneda;
    private double monto;
    private String pedidoId;     // llega como String
    private String proveedorId;
}
