package com.pharmacore.pharmacore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "detalles_formulas")
public class DetallesFormulas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_formula")
    private Long idDetalleFormula;

    @NotNull(message = "La fórmula es obligatoria")
    @Column(name = "id_formula", nullable = false)
    private Long idFormula;

    @NotNull(message = "El producto es obligatorio")
    @Column(name = "id_producto", nullable = false)
    private Integer idProducto;

    @NotBlank(message = "La dosis es obligatoria")
    @Column(nullable = false, length = 100)
    private String dosis;

    @NotBlank(message = "La frecuencia es obligatoria")
    @Column(nullable = false, length = 100)
    private String frecuencia;

    @NotBlank(message = "La duración del tratamiento es obligatoria")
    @Column(name = "duracion_tratamiento", nullable = false, length = 100)
    private String duracionTratamiento;
}
