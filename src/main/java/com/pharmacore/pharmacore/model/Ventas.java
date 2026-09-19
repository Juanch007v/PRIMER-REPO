package com.pharmacore.pharmacore.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ventas")
public class Ventas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Long idVenta;

    @Column(name = "numero_factura", nullable = false, length = 50)
    private String numeroFactura;

    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(name = "id_empleado", nullable = false)
    private Long idEmpleado;

    @Column(name = "fecha_venta")
    private LocalDateTime fechaVenta = LocalDateTime.now();

    @Column(precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "impuesto_iva", precision = 12, scale = 2)
    private BigDecimal impuestoIva = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "metodo_pago")
    private String metodoPago = "EFECTIVO";

    private String estado = "PAGADA";
}
