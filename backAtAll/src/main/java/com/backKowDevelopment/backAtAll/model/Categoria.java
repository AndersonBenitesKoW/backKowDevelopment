package com.backKowDevelopment.backAtAll.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    private String id;
    private boolean activo;
    private Date createdAt;
    private String descripcion;
    private String nombre;
    private String slug;
}