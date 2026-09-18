package com.pharmacore.pharmacore.view;

import com.pharmacore.pharmacore.model.Devoluciones;
import com.pharmacore.pharmacore.model.Usuarios;
import com.pharmacore.pharmacore.repository.*;
import com.pharmacore.pharmacore.service.InventarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
public class DevolucionesView {

    @Autowired
    private DevolucionesRepository devolucionesRepository;

    @Autowired
    private ProductosRepository productosRepository;

    @Autowired
    private VentasRepository ventasRepository;

    @Autowired
    private ComprasRepository comprasRepository;

    @Autowired
    private LotesRepository lotesRepository;

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private InventarioService inventarioService;

    // LISTA
    @GetMapping("/view/devoluciones")
    public String lista(Model model) {
        model.addAttribute("devoluciones", devolucionesRepository.findAllByOrderByFechaDevolucionDesc());
        cargarNombres(model);
        return "devoluciones/devoluciones";
    }

    // FORMULARIO
    @GetMapping("/view/devoluciones/form")
    public String form(Model model) {
        model.addAttribute("devolucion", new Devoluciones());
        cargarListasApoyo(model);
        return "devoluciones/devolucionesForm";
    }

    // GUARDAR
    @Transactional
    @PostMapping("/view/devoluciones/guardar")
    public String guardar(@Valid @ModelAttribute("devolucion") Devoluciones devolucion,
                           BindingResult bindingResult,
                           @RequestParam(required = false) Integer idLote,
                           Authentication authentication,
                           RedirectAttributes ra) {

        // Bean Validation (@NotNull/@Min en la entidad): campos obligatorios y cantidad > 0.
        if (bindingResult.hasErrors()) {
            String mensaje = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .distinct()
                    .collect(Collectors.joining(" "));
            ra.addFlashAttribute("error", mensaje);
            return "redirect:/view/devoluciones/form";
        }

        try {
            // id_usuario_regente no se pide en el formulario: se resuelve del usuario logueado.
            Usuarios usuario = usuariosRepository.findByUsernameIgnoreCase(authentication.getName());
            if (usuario == null) {
                throw new IllegalStateException("No se pudo identificar al usuario que registra la devolución.");
            }
            devolucion.setIdUsuarioRegente(usuario.getIdUsuario());
            if (devolucion.getFechaDevolucion() == null) {
                devolucion.setFechaDevolucion(LocalDateTime.now());
            }

            boolean afectaInventario = "PROVEEDOR".equals(devolucion.getTipoDevolucion())
                    || ("CLIENTE".equals(devolucion.getTipoDevolucion()) && "APTO_PARA_REINGRESO".equals(devolucion.getEstadoProducto()));

            if (afectaInventario) {
                String tipoMovimiento = "PROVEEDOR".equals(devolucion.getTipoDevolucion())
                        ? "DEVOLUCION_PROVEEDOR" : "DEVOLUCION_CLIENTE";
                inventarioService.registrarMovimiento(
                        tipoMovimiento,
                        devolucion.getIdProducto(),
                        idLote,
                        devolucion.getCantidad(),
                        usuario.getIdUsuario().intValue(),
                        "Devolución " + devolucion.getTipoDevolucion() + " - " + devolucion.getMotivo()
                );
            }

            devolucionesRepository.save(devolucion);
            ra.addFlashAttribute("mensaje", afectaInventario
                    ? "Devolución registrada: el inventario fue actualizado."
                    : "Devolución registrada. No se modificó el inventario porque el producto no vuelve a estar disponible para la venta.");
            return "redirect:/view/devoluciones";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo registrar la devolución: " + e.getMessage());
            return "redirect:/view/devoluciones/form";
        }
    }

    private void cargarListasApoyo(Model model) {
        model.addAttribute("productos", productosRepository.findAll());
        model.addAttribute("ventas", ventasRepository.findAll());
        model.addAttribute("compras", comprasRepository.findAll());
        model.addAttribute("tipos", Devoluciones.TIPOS);
        model.addAttribute("motivos", Devoluciones.MOTIVOS);
        model.addAttribute("estadosProducto", Devoluciones.ESTADOS_PRODUCTO);

        // Lotes por producto, para el <select> dependiente en el formulario (igual patrón que el POS).
        List<Map<String, Object>> lotesParaJs = lotesRepository.findAll().stream()
                .map(l -> Map.<String, Object>of(
                        "idLote", l.getIdLote(),
                        "idProducto", l.getIdProducto(),
                        "numeroLote", l.getNumeroLote()
                ))
                .collect(Collectors.toList());
        model.addAttribute("lotes", lotesParaJs);
    }

    private void cargarNombres(Model model) {
        model.addAttribute("nombresProductos", productosRepository.findAll().stream()
                .collect(Collectors.toMap(p -> p.getIdProducto(), p -> p.getNombreComercial())));
        model.addAttribute("nombresUsuarios", usuariosRepository.findAll().stream()
                .collect(Collectors.toMap(u -> u.getIdUsuario(), u -> u.getUsername())));
        model.addAttribute("numerosFactura", ventasRepository.findAll().stream()
                .collect(Collectors.toMap(v -> v.getIdVenta(), v -> v.getNumeroFactura())));
        model.addAttribute("facturasProveedor", comprasRepository.findAll().stream()
                .collect(Collectors.toMap(c -> c.getIdCompra(), c -> c.getNumeroFacturaProveedor())));
    }
}
