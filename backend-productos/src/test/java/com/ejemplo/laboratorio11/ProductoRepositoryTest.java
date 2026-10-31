package com.ejemplo.laboratorio11;

import com.ejemplo.laboratorio11.model.Producto;
import com.ejemplo.laboratorio11.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @BeforeEach
    void setUp() {
        productoRepository.deleteAll();
        productoRepository.saveAll(List.of(
                new Producto(null, "Laptop Lenovo", 2500.0, 10),
                new Producto(null, "Mouse Logitech", 80.0, 25),
                new Producto(null, "Teclado mecanico", 180.0, 15),
                new Producto(null, "Monitor 24 pulgadas", 650.0, 8),
                new Producto(null, "Webcam Full HD", 160.0, 12),
                new Producto(null, "SSD 1TB", 290.0, 9),
                new Producto(null, "UPS 1000VA", 520.0, 3) // Stock < 5 (INSUFICIENTE)
        ));
    }

    @Test
    @DisplayName("Debe listar productos paginados con tamaño 5 y metadata correcta")
    void testListarPaginado() {
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("id").ascending());
        Page<Producto> pagina = productoRepository.findAll(pageRequest);

        assertThat(pagina).isNotNull();
        assertThat(pagina.getNumberOfElements()).isEqualTo(5);
        assertThat(pagina.getTotalElements()).isEqualTo(7);
        assertThat(pagina.getTotalPages()).isEqualTo(2);
        assertThat(pagina.getNumber()).isEqualTo(0);
        assertThat(pagina.isFirst()).isTrue();
        assertThat(pagina.isLast()).isFalse();
    }

    @Test
    @DisplayName("Debe buscar productos por nombre insensible a mayusculas de forma paginada")
    void testBuscarPorNombreContainingIgnoreCase() {
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("nombre").ascending());
        Page<Producto> pagina = productoRepository.findByNombreContainingIgnoreCase("logitech", pageRequest);

        assertThat(pagina.getNumberOfElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).getNombre()).isEqualTo("Mouse Logitech");
        assertThat(pagina.getContent().get(0).getEstadoStock()).isEqualTo("OK");
    }

    @Test
    @DisplayName("Debe calcular correctamente el estado de stock transitorio")
    void testEstadoStockTransient() {
        Producto productoOk = new Producto(null, "Mouse", 50.0, 5);
        Producto productoInsuficiente = new Producto(null, "UPS", 500.0, 4);

        assertThat(productoOk.getEstadoStock()).isEqualTo("OK");
        assertThat(productoInsuficiente.getEstadoStock()).isEqualTo("INSUFICIENTE");
    }
}
