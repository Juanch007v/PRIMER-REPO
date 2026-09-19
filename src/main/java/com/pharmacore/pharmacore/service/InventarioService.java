package com.pharmacore.pharmacore.service;

import com.pharmacore.pharmacore.model.Lotes;
import com.pharmacore.pharmacore.model.MovimientosInventario;
import com.pharmacore.pharmacore.model.Productos;
import com.pharmacore.pharmacore.repository.LotesRepository;
import com.pharmacore.pharmacore.repository.MovimientosInventarioRepository;
import com.pharmacore.pharmacore.repository.ProductosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class InventarioService {

    @Autowired
    private ProductosRepository productosRepository;

    @Autowired
    private LotesRepository lotesRepository;

    @Autowired
    private MovimientosInventarioRepository movimientosRepository;

    // Tipos que SUMAN al stock (entra mercancía)
    private static final Set<String> TIPOS_ENTRADA = Set.of("ENTRADA_COMPRA", "DEVOLUCION_CLIENTE");

    // Tipos que RESTAN del stock (sale mercancía)
    private static final Set<String> TIPOS_SALIDA = Set.of("SALIDA_VENTA", "AJUSTE_PERDIDA", "AJUSTE_DANIO", "DEVOLUCION_PROVEEDOR");

    public MovimientosInventario registrarMovimiento(String tipoMovimiento, Integer idProducto, Integer idLote,
                                                       Integer cantidad, Integer idUsuario, String motivo) {

        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad del movimiento debe ser mayor a cero.");
        }
        if (!TIPOS_ENTRADA.contains(tipoMovimiento) && !TIPOS_SALIDA.contains(tipoMovimiento)) {
            throw new IllegalArgumentException("Tipo de movimiento no reconocido: " + tipoMovimiento);
        }

        Productos producto = productosRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("El producto indicado no existe."));

        int existenciaAnterior = producto.getStockTotal() != null ? producto.getStockTotal() : 0;
        boolean esEntrada = TIPOS_ENTRADA.contains(tipoMovimiento);
        int nuevaExistencia = esEntrada ? existenciaAnterior + cantidad : existenciaAnterior - cantidad;

        if (nuevaExistencia < 0) {
            throw new IllegalStateException("La operación dejaría el stock de \"" + producto.getNombreComercial()
                    + "\" en negativo (disponible: " + existenciaAnterior + ", solicitado: " + cantidad + ").");
        }

        // Si el movimiento está atado a un lote específico, se actualiza también su cantidad_actual
        if (idLote != null) {
            Lotes lote = lotesRepository.findById(idLote)
                    .orElseThrow(() -> new IllegalArgumentException("El lote indicado no existe."));
            int cantidadLoteAnterior = lote.getCantidadActual() != null ? lote.getCantidadActual() : 0;
            int nuevaCantidadLote = esEntrada ? cantidadLoteAnterior + cantidad : cantidadLoteAnterior - cantidad;
            if (nuevaCantidadLote < 0) {
                throw new IllegalStateException("La operación dejaría el lote \"" + lote.getNumeroLote() + "\" en negativo.");
            }
            lote.setCantidadActual(nuevaCantidadLote);
            lotesRepository.save(lote);
        }

        producto.setStockTotal(nuevaExistencia);
        productosRepository.save(producto);

        MovimientosInventario movimiento = MovimientosInventario.builder()
                .tipoMovimiento(tipoMovimiento)
                .idProducto(idProducto)
                .idLote(idLote)
                .cantidad(cantidad)
                .existenciaAnterior(existenciaAnterior)
                .nuevaExistencia(nuevaExistencia)
                .idUsuario(idUsuario)
                .motivo(motivo)
                .build();

        return movimientosRepository.save(movimiento);
    }
}
