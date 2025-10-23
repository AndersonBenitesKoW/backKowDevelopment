package com.backKowDevelopment.backAtAll.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoItem {
    private String id;
    private int cantidad;
    private String categoriaId;
    private String concepto;
    private double precioUnitario;
    private double descuento;
    private double impuestos;
    private double total;
}