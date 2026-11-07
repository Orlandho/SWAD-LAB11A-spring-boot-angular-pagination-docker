package com.ejemplo.laboratorio11.service;

import com.ejemplo.laboratorio11.dto.VentaRequest;
import com.ejemplo.laboratorio11.model.Producto;
import com.ejemplo.laboratorio11.model.Venta;
import com.ejemplo.laboratorio11.repository.ProductoRepository;
import com.ejemplo.laboratorio11.repository.VentaRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VentaService {
    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;

    public VentaService(ProductoRepository productoRepository, VentaRepository ventaRepository) {
        this.productoRepository = productoRepository;
        this.ventaRepository = ventaRepository;
    }

    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    public List<Venta> buscarPorFechaYProducto(LocalDateTime inicio, LocalDateTime fin, String nombre) {
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("El inicio debe ser anterior o igual al fin");
        }
        return ventaRepository.buscarPorFechaYProducto(inicio, fin, nombre);
    }

    // Las dos escrituras pertenecen a la misma transaccion. El rollback es automatico.
    @Transactional(rollbackFor = Exception.class)
    public Venta registrarVenta(VentaRequest request) {
        if (request == null || request.getProductoId() == null) {
            throw new IllegalArgumentException("Debe indicar el producto");
        }
        if (request.getCantidad() == null || request.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        if (producto.getStock() < request.getCantidad()) {
            throw new IllegalArgumentException("Stock insuficiente para realizar la venta");
        }

        producto.setStock(producto.getStock() - request.getCantidad());
        productoRepository.saveAndFlush(producto);

        Venta venta = new Venta();
        venta.setProducto(producto);
        venta.setCantidad(request.getCantidad());
        venta.setPrecioUnitario(producto.getPrecio());
        venta.setTotal(producto.getPrecio() * request.getCantidad());
        venta.setFecha(LocalDateTime.now());

        Venta guardada = ventaRepository.saveAndFlush(venta);

        // La excepcion ocurre ANTES del COMMIT: se revierten UPDATE e INSERT.
        if (Boolean.TRUE.equals(request.getSimularError())) {
            throw new RuntimeException("Error simulado: Spring debe ejecutar ROLLBACK");
        }
        return guardada;
    }
}
