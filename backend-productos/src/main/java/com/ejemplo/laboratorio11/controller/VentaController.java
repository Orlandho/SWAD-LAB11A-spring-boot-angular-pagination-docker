package com.ejemplo.laboratorio11.controller;

import com.ejemplo.laboratorio11.dto.VentaRequest;
import com.ejemplo.laboratorio11.model.Venta;
import com.ejemplo.laboratorio11.service.VentaService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
public class VentaController {
    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public List<Venta> listar() {
        return ventaService.listarVentas();
    }

    // Reto adicional de la pagina 19 del laboratorio.
    @GetMapping("/buscar")
    public List<Venta> buscar(
            @RequestParam String inicio,
            @RequestParam String fin,
            @RequestParam(defaultValue = "") String nombre) {
        return ventaService.buscarPorFechaYProducto(
                LocalDateTime.parse(inicio), LocalDateTime.parse(fin), nombre);
    }

    @PostMapping
    public ResponseEntity<Venta> registrar(@RequestBody VentaRequest request) {
        Venta venta = ventaService.registrarVenta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(venta);
    }
}
