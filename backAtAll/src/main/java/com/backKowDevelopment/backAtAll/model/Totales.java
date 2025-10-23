package com.backKowDevelopment.backAtAll.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Totales {
    private double descuento;
    private double impuestos;
    private double subtotal;
    private double total;
}