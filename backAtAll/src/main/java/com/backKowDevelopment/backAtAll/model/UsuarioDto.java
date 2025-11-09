package com.backKowDevelopment.backAtAll.model;

import java.util.Date;

public record UsuarioDto( String id,
                          String nombres,
                          String apellidos,
                          String email,
                          String rol,
                          Boolean active,
                          Date createdAt,
                          Date updatedAt) {}




