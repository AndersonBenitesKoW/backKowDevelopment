package com.backKowDevelopment.backAtAll.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Direccion {
    private String ciudad;
    private String distrito;
    private String linea;  // ej. "José Crespo Y Castillo 1008..."
    private String pais;
}