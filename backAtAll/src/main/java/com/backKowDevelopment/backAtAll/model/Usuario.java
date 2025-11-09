package com.backKowDevelopment.backAtAll.model;

import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private String id;
    private boolean active;
    @ServerTimestamp
    private Date createdAt;
    private String nombres;
    private String apellidos;
    private String contraseña;
    private String email;
    private String rol;

    @ServerTimestamp
    private Date updatedAt;  // ✅ Nuevo campo para registrar la última modificación
}




