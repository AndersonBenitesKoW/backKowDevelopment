package com.backKowDevelopment.backAtAll.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {
    private String id;
    private String clienteId;
    private Date createdAt;
    private String numero;
    private String pedidoId;
    private String serie;
    private String tipoComprobante;
    private Totales totales;
    private List<VentaDetalle> detalles;
}