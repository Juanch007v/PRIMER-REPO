package com.pharmacore.pharmacore.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(basePackages = "com.pharmacore.pharmacore.view")
public class GlobalWebExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String manejarIntegridadDatos(DataIntegrityViolationException ex, HttpServletRequest request, RedirectAttributes ra) {
        ra.addFlashAttribute("error", "No se pudo completar la operación: el registro está relacionado con otros " +
                "datos del sistema (ventas, compras, movimientos de inventario, etc.) y no se puede eliminar ni " +
                "guardar así. Si necesita desactivarlo, use el estado en vez de eliminarlo.");
        return "redirect:" + destinoSeguro(request);
    }

    @ExceptionHandler(BindException.class)
    public String manejarErroresDeValidacion(BindException ex, HttpServletRequest request, RedirectAttributes ra) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getDefaultMessage())
                .filter(m -> m != null && !m.isBlank())
                .distinct()
                .reduce((a, b) -> a + " " + b)
                .orElse("Revise los datos del formulario: hay campos obligatorios sin completar o con un valor inválido.");
        ra.addFlashAttribute("error", mensaje);
        return "redirect:" + destinoSeguro(request);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public String manejarErrorDeNegocio(RuntimeException ex, HttpServletRequest request, RedirectAttributes ra) {
        ra.addFlashAttribute("error", ex.getMessage() != null ? ex.getMessage() : "No se pudo completar la operación.");
        return "redirect:" + destinoSeguro(request);
    }

    @ExceptionHandler(Exception.class)
    public String manejarErrorGenerico(Exception ex, HttpServletRequest request, RedirectAttributes ra) {
        ra.addFlashAttribute("error", "Ocurrió un error inesperado al procesar la solicitud. " +
                "Si el problema persiste, contacte al administrador del sistema.");
        return "redirect:" + destinoSeguro(request);
    }

    private String destinoSeguro(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("://" + request.getServerName())) {
            return referer;
        }
        return "/view/home";
    }
}
