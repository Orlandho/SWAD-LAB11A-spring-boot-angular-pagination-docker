package com.ejemplo.laboratorio11.controller;

import com.ejemplo.laboratorio11.model.Producto;
import com.ejemplo.laboratorio11.service.ProductoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/pagina")
    public Page<Producto> listarPaginado(
            @PageableDefault(size = 5, sort = "id")
            Pageable pageable) {
        return productoService.listarPaginado(pageable);
    }

    @GetMapping("/pagina/buscar")
    public Page<Producto> buscar(
            @RequestParam(required = false, defaultValue = "") String nombre,
            @PageableDefault(size = 5, sort = "id")
            Pageable pageable) {
        return productoService.buscarPorNombre(nombre, pageable);
    }
    @GetMapping("/pagina/avanzada")
    public Page<Producto> buscarAvanzado(
            @RequestParam(defaultValue = "") String nombre,
            @RequestParam(defaultValue = "0") Double precioMin,
            @RequestParam(defaultValue = "999999") Double precioMax,
            @RequestParam(defaultValue = "0") Integer stockMin,
            @PageableDefault(size = 5, sort = "precio")
            Pageable pageable) {
        return productoService.buscarAvanzado(
                nombre, precioMin, precioMax, stockMin, pageable);
    }
}
