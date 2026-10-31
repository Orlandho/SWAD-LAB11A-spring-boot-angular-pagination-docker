package com.ejemplo.laboratorio11.service;

import com.ejemplo.laboratorio11.model.Producto;
import com.ejemplo.laboratorio11.repository.ProductoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Page<Producto> listarPaginado(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }

    public Page<Producto> buscarPorNombre(
            String nombre,
            Pageable pageable) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return productoRepository.findAll(pageable);
        }
        return productoRepository
                .findByNombreContainingIgnoreCase(nombre.trim(), pageable);
    }
}
