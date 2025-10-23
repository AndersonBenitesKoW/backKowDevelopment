package com.backKowDevelopment.backAtAll.model;

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
    private Date createdAt;
    private String displayName;
    private String email;
    private String idusuario;
    private String rol;
}