package com.pharmacore.pharmacore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "formulas_medicas")
public class FormulasMedicas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_formula")
    private Long idFormula;

    @NotNull(message = "El paciente es obligatorio")
    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @NotBlank(message = "El nombre del médico es obligatorio")
    @Column(name = "nombre_medico", nullable = false, length = 150)
    private String nombreMedico;

    @NotBlank(message = "La tarjeta profesional es obligatoria")
    @Column(name = "tarjeta_profesional", nullable = false, length = 50)
    private String tarjetaProfesional;

    @Column(name = "entidad_salud", length = 100)
    private String entidadSalud;

    @Column(name = "fecha_expedicion", nullable = false)
    private LocalDate fechaExpedicion;

    @Column(name = "vigencia_dias")
    private Integer vigenciaDias = 30;

    @Column(name = "archivo_adjunto_url", length = 255)
    private String archivoAdjuntoUrl;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}
