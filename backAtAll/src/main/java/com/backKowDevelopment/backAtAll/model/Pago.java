package com.backKowDevelopment.backAtAll.model;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago {
    @DocumentId
    private String id;

    @ServerTimestamp
    private Date createdAt;

    private String estado;
    private String metodo;
    private String moneda;
    private double monto;

    // 🔥 CAMBIO AQUÍ: usar DocumentReference
    private DocumentReference pedidoId;

    private String proveedorId;

    @ServerTimestamp
    private Date updatedAt;
}