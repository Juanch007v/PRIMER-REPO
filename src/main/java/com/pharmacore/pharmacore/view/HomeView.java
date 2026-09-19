package com.pharmacore.pharmacore.view;

import com.pharmacore.pharmacore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;


@Controller
public class HomeView {

    @Autowired
    private ProductosRepository productosRepository;

    @Autowired
    private VentasRepository ventasRepository;

    @Autowired
    private LotesRepository lotesRepository;

    @Autowired
    private ClientesRepository clientesRepository;

    @GetMapping({"/", "/view/home"})
    public String home(Model model) {
        LocalDate hoy = LocalDate.now();

        long totalProductos = productosRepository.count();
        long totalClientes = clientesRepository.count();
        long totalVentas = ventasRepository.count();

        List<?> lotes = lotesRepository.findAll();
        long lotesVencidos = lotes.stream()
                .filter(l -> ((com.pharmacore.pharmacore.model.Lotes) l).isVencido())
                .count();
        long lotesPorVencer = lotes.stream()
                .filter(l -> "PROXIMO_A_VENCER".equals(((com.pharmacore.pharmacore.model.Lotes) l).getEstadoCalculado()))
                .count();

        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("totalVentas", totalVentas);
        model.addAttribute("lotesVencidos", lotesVencidos);
        model.addAttribute("lotesPorVencer", lotesPorVencer);

        return "home/home";
    }
}
