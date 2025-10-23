package com.backKowDevelopment.backAtAll.model;

import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    private String id;
    private String apellidos;
    private String nombres;
    private String empresa;
    private String documentoIdentidad;
    private String email;
    private String telefono;
    @ServerTimestamp
    private Date createdAt;
    private Direccion direccion;
}