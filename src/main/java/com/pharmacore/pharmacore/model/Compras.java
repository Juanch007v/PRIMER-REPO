package com.pharmacore.pharmacore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "compras")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compras {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Integer idCompra;

    @Column(name = "numero_factura_proveedor", length = 50)
    private String numeroFacturaProveedor;

    @NotNull(message = "El proveedor es obligatorio")
    @Column(name = "id_proveedor", nullable = false)
    private Integer idProveedor;

    @NotNull(message = "El empleado es obligatorio")
    @Column(name = "id_empleado", nullable = false)
    private Long idEmpleado;

    @Builder.Default
    @Column(name = "fecha_compra")
    private LocalDateTime fechaCompra = LocalDateTime.now();

    @Builder.Default
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "impuestos", precision = 12, scale = 2)
    private BigDecimal impuestos = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "forma_pago")
    private String formaPago = "CREDITO_PROVEEDOR";

    @Builder.Default
    @Column(name = "estado")
    private String estado = "PENDIENTE";
}
