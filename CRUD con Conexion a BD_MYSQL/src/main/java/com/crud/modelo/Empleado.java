package com.crud.modelo;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="Empleados")
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="nombre",nullable = false)
    private String nombre;
    @Column(name="area")
    private String area;
    @Column(name="edad")
    private Integer edad;
    @Column(name="correo_electronico",nullable = true)
    private String correo_electronico;
    @Column(name="sueldo")
    private Double sueldo;
}
