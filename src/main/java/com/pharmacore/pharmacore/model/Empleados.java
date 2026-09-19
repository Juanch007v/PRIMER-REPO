package com.pharmacore.pharmacore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity

//se trae la tabla empleados
@Table(name = "empleados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

//se cra la clase para poder traer los datos
public class Empleados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_empleado;

    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombre_completo;

    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipo_documento;

    @NotBlank(message = "El número de documento es obligatorio")
    private String numero_documento;

    private String telefono;

    private String direccion;

    private String correo;

    @NotBlank(message = "El cargo es obligatorio")
    private String cargo;

    @NotNull(message = "El salario es obligatorio")
    private BigDecimal salario;

    private String estado;

    @Transient
    private Usuarios usuario;
}