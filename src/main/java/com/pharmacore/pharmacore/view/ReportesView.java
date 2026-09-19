package com.pharmacore.pharmacore.view;

import com.pharmacore.pharmacore.model.*;
import com.pharmacore.pharmacore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class ReportesView {

    @Autowired
    private VentasRepository ventasRepository;

    @Autowired
    private DetallesVentasRepository detallesVentasRepository;

    @Autowired
    private ComprasRepository comprasRepository;

    @Autowired
    private ProveedoresRepository proveedoresRepository;

    @Autowired
    private ProductosRepository productosRepository;

    @Autowired
    private LotesRepository lotesRepository;

    @GetMapping("/view/reportes")
    public String reportes(@RequestParam(required = false) String desde,
                            @RequestParam(required = false) String hasta,
                            Model model) {

        LocalDate hoy = LocalDate.now();
        LocalDate fechaDesde = parseFecha(desde, hoy.withDayOfMonth(1));
        LocalDate fechaHasta = parseFecha(hasta, hoy);
        LocalDateTime desdeDT = fechaDesde.atStartOfDay();
        LocalDateTime hastaDT = fechaHasta.atTime(23, 59, 59);

        // ---- Ventas del período ----
        List<Ventas> ventasEnRango = ventasRepository.findAll().stream()
                .filter(v -> "PAGADA".equals(v.getEstado()))
                .filter(v -> v.getFechaVenta() != null
                        && !v.getFechaVenta().isBefore(desdeDT) && !v.getFechaVenta().isAfter(hastaDT))
                .sorted(Comparator.comparing(Ventas::getFechaVenta).reversed())
                .collect(Collectors.toList());

        BigDecimal totalIngresos = ventasEnRango.stream()
                .map(v -> v.getTotal() != null ? v.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal ticketPromedio = ventasEnRango.isEmpty() ? BigDecimal.ZERO
                : totalIngresos.divide(BigDecimal.valueOf(ventasEnRango.size()), 2, RoundingMode.HALF_UP);

        // ---- Productos más vendidos del período ----
        Set<Long> idsVentasEnRango = ventasEnRango.stream().map(Ventas::getIdVenta).collect(Collectors.toSet());
        Map<Integer, Integer> cantidadPorProducto = detallesVentasRepository.findAll().stream()
                .filter(d -> d.getVenta() != null && idsVentasEnRango.contains(d.getVenta().getIdVenta()))
                .collect(Collectors.groupingBy(DetallesVentas::getIdProducto, Collectors.summingInt(DetallesVentas::getCantidad)));

        Map<Integer, String> nombresProductos = productosRepository.findAll().stream()
                .collect(Collectors.toMap(Productos::getIdProducto, Productos::getNombreComercial));

        List<Map<String, Object>> masVendidos = cantidadPorProducto.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(10)
                .map(e -> Map.<String, Object>of(
                        "producto", nombresProductos.getOrDefault(e.getKey(), "Producto #" + e.getKey()),
                        "cantidad", e.getValue()))
                .collect(Collectors.toList());

        // ---- Compras por proveedor del período ----
        Map<Integer, String> nombresProveedores = proveedoresRepository.findAll().stream()
                .collect(Collectors.toMap(Proveedores::getId_proveedor, Proveedores::getRazon_social));

        Map<Integer, BigDecimal> comprasPorProveedor = comprasRepository.findAll().stream()
                .filter(c -> c.getFechaCompra() != null
                        && !c.getFechaCompra().isBefore(desdeDT) && !c.getFechaCompra().isAfter(hastaDT))
                .collect(Collectors.groupingBy(Compras::getIdProveedor,
                        Collectors.reducing(BigDecimal.ZERO, c -> c.getTotal() != null ? c.getTotal() : BigDecimal.ZERO, BigDecimal::add)));

        List<Map<String, Object>> comprasPorProveedorList = comprasPorProveedor.entrySet().stream()
                .sorted(Map.Entry.<Integer, BigDecimal>comparingByValue().reversed())
                .map(e -> Map.<String, Object>of(
                        "proveedor", nombresProveedores.getOrDefault(e.getKey(), "Proveedor #" + e.getKey()),
                        "total", e.getValue()))
                .collect(Collectors.toList());

        // ---- Stock bajo (foto actual del inventario, no depende del rango de fechas) ----
        List<Productos> stockBajo = productosRepository.findAll().stream()
                .filter(Productos::isStockBajo)
                .sorted(Comparator.comparing(Productos::getStockTotal))
                .collect(Collectors.toList());

        // ---- Lotes vencidos / próximos a vencer (misma regla que la pantalla de vencimientos) ----
        List<Lotes> lotesVencidos = lotesRepository.findAll().stream()
                .filter(l -> "VENCIDO".equals(l.getEstadoCalculado()))
                .collect(Collectors.toList());
        List<Lotes> lotesProximos = lotesRepository.findAll().stream()
                .filter(l -> "PROXIMO_A_VENCER".equals(l.getEstadoCalculado()))
                .collect(Collectors.toList());

        model.addAttribute("desde", fechaDesde);
        model.addAttribute("hasta", fechaHasta);
        model.addAttribute("ventasEnRango", ventasEnRango);
        model.addAttribute("totalVentas", ventasEnRango.size());
        model.addAttribute("totalIngresos", totalIngresos);
        model.addAttribute("ticketPromedio", ticketPromedio);
        model.addAttribute("masVendidos", masVendidos);
        model.addAttribute("comprasPorProveedor", comprasPorProveedorList);
        model.addAttribute("stockBajo", stockBajo);
        model.addAttribute("lotesVencidos", lotesVencidos);
        model.addAttribute("lotesProximos", lotesProximos);
        model.addAttribute("nombresProductosLote", nombresProductos);

        return "reportes/reportes";
    }

    // Exporta a CSV (se abre directo en Excel) las ventas del rango seleccionado.
    @GetMapping("/view/reportes/exportar/ventas")
    public ResponseEntity<byte[]> exportarVentasCsv(@RequestParam(required = false) String desde,
                                                      @RequestParam(required = false) String hasta) {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaDesde = parseFecha(desde, hoy.withDayOfMonth(1));
        LocalDate fechaHasta = parseFecha(hasta, hoy);
        LocalDateTime desdeDT = fechaDesde.atStartOfDay();
        LocalDateTime hastaDT = fechaHasta.atTime(23, 59, 59);

        List<Ventas> ventasEnRango = ventasRepository.findAll().stream()
                .filter(v -> "PAGADA".equals(v.getEstado()))
                .filter(v -> v.getFechaVenta() != null
                        && !v.getFechaVenta().isBefore(desdeDT) && !v.getFechaVenta().isAfter(hastaDT))
                .sorted(Comparator.comparing(Ventas::getFechaVenta))
                .collect(Collectors.toList());

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(salida, true, StandardCharsets.UTF_8)) {
            writer.write('\uFEFF'); // BOM: para que Excel detecte UTF-8 y no dañe tildes/ñ
            writer.println("N Factura;Fecha;Subtotal;Descuento;IVA;Total;Metodo de pago;Estado");
            for (Ventas v : ventasEnRango) {
                writer.println(String.join(";",
                        csv(v.getNumeroFactura()),
                        csv(v.getFechaVenta().format(fmt)),
                        csv(String.valueOf(v.getSubtotal())),
                        csv(String.valueOf(v.getDescuento())),
                        csv(String.valueOf(v.getImpuestoIva())),
                        csv(String.valueOf(v.getTotal())),
                        csv(v.getMetodoPago()),
                        csv(v.getEstado())
                ));
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("reporte-ventas-" + fechaDesde + "_a_" + fechaHasta + ".csv").build());
        return ResponseEntity.ok().headers(headers).body(salida.toByteArray());
    }

    private String csv(String valor) {
        return valor == null ? "" : valor.replace(";", ",");
    }

    private LocalDate parseFecha(String valor, LocalDate porDefecto) {
        if (valor == null || valor.isBlank()) return porDefecto;
        try {
            return LocalDate.parse(valor);
        } catch (Exception e) {
            return porDefecto;
        }
    }
}
