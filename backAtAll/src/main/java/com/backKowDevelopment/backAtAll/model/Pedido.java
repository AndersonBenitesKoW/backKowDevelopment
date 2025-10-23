package com.backKowDevelopment.backAtAll.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    private String id;
    private String clienteId;
    private Date createdAt;
    private String createdBy;
    private String estado;
    private String moneda;
    private Totales totales;
    private Date updatedAt;
    private List<PedidoItem> items;
}