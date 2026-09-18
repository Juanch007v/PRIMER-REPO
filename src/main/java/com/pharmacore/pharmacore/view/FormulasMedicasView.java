package com.pharmacore.pharmacore.view;

import com.pharmacore.pharmacore.model.DetallesFormulas;
import com.pharmacore.pharmacore.model.FormulasMedicas;
import com.pharmacore.pharmacore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class FormulasMedicasView {

    @Autowired
    private FormulasMedicasRepository formulasRepository;

    @Autowired
    private DetallesFormulasRepository detallesFormulasRepository;

    @Autowired
    private ClientesRepository clientesRepository;

    @Autowired
    private ProductosRepository productosRepository;

    // LISTA
    @GetMapping("/view/formulas")
    public String lista(Model model) {
        model.addAttribute("formulas", formulasRepository.findAll());
        cargarNombres(model);
        return "formulas/formulas";
    }

    // FORMULARIO CREAR (con líneas de medicamentos dinámicas)
    @GetMapping("/view/formulas/form")
    public String form(Model model) {
        model.addAttribute("formulaMedica", new FormulasMedicas());
        cargarListasApoyo(model);
        return "formulas/formulasForm";
    }

    // GUARDAR fórmula + sus líneas de medicamentos
    @PostMapping("/view/formulas/save")
    public String save(@ModelAttribute FormulasMedicas formulaMedica,
                        @RequestParam(required = false) List<Integer> idProductos,
                        @RequestParam(required = false) List<String> dosisList,
                        @RequestParam(required = false) List<String> frecuenciaList,
                        @RequestParam(required = false) List<String> duracionTratamientoList,
                        RedirectAttributes ra) {
        try {
            if (formulaMedica.getFechaExpedicion() == null) {
                formulaMedica.setFechaExpedicion(LocalDate.now());
            }
            FormulasMedicas formulaGuardada = formulasRepository.save(formulaMedica);

            if (idProductos != null) {
                for (int i = 0; i < idProductos.size(); i++) {
                    DetallesFormulas detalle = new DetallesFormulas();
                    detalle.setIdFormula(formulaGuardada.getIdFormula());
                    detalle.setIdProducto(idProductos.get(i));
                    detalle.setDosis(dosisList != null && dosisList.size() > i ? dosisList.get(i) : "");
                    detalle.setFrecuencia(frecuenciaList != null && frecuenciaList.size() > i ? frecuenciaList.get(i) : "");
                    detalle.setDuracionTratamiento(duracionTratamientoList != null && duracionTratamientoList.size() > i ? duracionTratamientoList.get(i) : "");
                    detallesFormulasRepository.save(detalle);
                }
            }

            ra.addFlashAttribute("mensaje", "Fórmula médica registrada exitosamente.");
            return "redirect:/view/formulas/detalle/" + formulaGuardada.getIdFormula();
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al registrar la fórmula médica: " + e.getMessage());
            return "redirect:/view/formulas/form";
        }
    }

    // DETALLE (fórmula + sus líneas de medicamentos)
    @GetMapping("/view/formulas/detalle/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        FormulasMedicas formula = formulasRepository.findById(id).orElse(null);
        if (formula == null) {
            return "redirect:/view/formulas";
        }
        model.addAttribute("formula", formula);
        model.addAttribute("detalles", detallesFormulasRepository.findByIdFormula(id));
        model.addAttribute("productos", productosRepository.findAll());
        cargarNombres(model);
        return "formulas/formulaDetalle";
    }

    // Mapas "ID -> nombre" para no mostrar IDs crudos en las plantillas.
    private void cargarNombres(Model model) {
        model.addAttribute("nombresClientes", clientesRepository.findAll().stream()
                .collect(Collectors.toMap(c -> c.getId_cliente(), c -> c.getNombreCompleto())));
        model.addAttribute("nombresProductos", productosRepository.findAll().stream()
                .collect(Collectors.toMap(p -> p.getIdProducto(), p -> p.getNombreComercial())));
    }

    // Agregar un medicamento adicional a una fórmula ya existente
    @PostMapping("/view/formulas/detalle/{id}/agregar")
    public String agregarDetalle(@PathVariable Long id,
                                  @RequestParam Integer idProducto,
                                  @RequestParam String dosis,
                                  @RequestParam String frecuencia,
                                  @RequestParam String duracionTratamiento,
                                  RedirectAttributes ra) {
        DetallesFormulas detalle = new DetallesFormulas();
        detalle.setIdFormula(id);
        detalle.setIdProducto(idProducto);
        detalle.setDosis(dosis);
        detalle.setFrecuencia(frecuencia);
        detalle.setDuracionTratamiento(duracionTratamiento);
        detallesFormulasRepository.save(detalle);
        ra.addFlashAttribute("mensaje", "Medicamento agregado a la fórmula.");
        return "redirect:/view/formulas/detalle/" + id;
    }

    // ELIMINAR una línea de medicamento de la fórmula
    @PostMapping("/view/formulas/detalle-linea/delete/{id}")
    public String eliminarDetalle(@PathVariable Long id, RedirectAttributes ra) {
        Long idFormulaRef = null;
        DetallesFormulas detalle = detallesFormulasRepository.findById(id).orElse(null);
        if (detalle != null) {
            idFormulaRef = detalle.getIdFormula();
            detallesFormulasRepository.deleteById(id);
            ra.addFlashAttribute("mensaje", "Medicamento eliminado de la fórmula.");
        }
        return idFormulaRef != null ? "redirect:/view/formulas/detalle/" + idFormulaRef : "redirect:/view/formulas";
    }

    // ELIMINAR fórmula completa
    @PostMapping("/view/formulas/delete/{id}")
    public String eliminarFormula(@PathVariable Long id, RedirectAttributes ra) {
        formulasRepository.deleteById(id);
        ra.addFlashAttribute("mensaje", "Fórmula médica eliminada.");
        return "redirect:/view/formulas";
    }

    private void cargarListasApoyo(Model model) {
        model.addAttribute("clientes", clientesRepository.findAll());
        model.addAttribute("productos", productosRepository.findAll());
    }
}
