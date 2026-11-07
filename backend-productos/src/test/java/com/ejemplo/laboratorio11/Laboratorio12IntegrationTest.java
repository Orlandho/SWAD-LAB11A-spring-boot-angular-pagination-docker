package com.ejemplo.laboratorio11;

import com.ejemplo.laboratorio11.model.Producto;
import com.ejemplo.laboratorio11.repository.ProductoRepository;
import com.ejemplo.laboratorio11.repository.VentaRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class Laboratorio12IntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ProductoRepository productos;
    @Autowired private VentaRepository ventas;

    private Long productoId;

    @BeforeEach
    void preparar() {
        ventas.deleteAll();
        productos.deleteAll();
        Producto producto = productos.save(new Producto(null, "Laptop Lenovo", 2500.0, 10));
        productoId = producto.getId();
        productos.saveAll(List.of(
                new Producto(null, "Mouse Logitech", 80.0, 25),
                new Producto(null, "Teclado mecanico", 180.0, 15),
                new Producto(null, "UPS 1000VA", 520.0, 3)
        ));
    }

    @Test
    @DisplayName("JPQL: cuatro parametros nombrados y metadata Page")
    void consultaAvanzada() throws Exception {
        mockMvc.perform(get("/api/productos/pagina/avanzada")
                        .param("nombre", "a")
                        .param("precioMin", "50")
                        .param("precioMax", "3000")
                        .param("stockMin", "5")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "precio,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.last", is(true)));
    }

    @Test
    @DisplayName("COMMIT: descuento de stock e INSERT de venta")
    void commit() throws Exception {
        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":" + productoId + ",\"cantidad\":2,\"simularError\":false}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cantidad", is(2)))
                .andExpect(jsonPath("$.total", is(5000.0)));

        assertThat(productos.findById(productoId).orElseThrow().getStock()).isEqualTo(8);
        assertThat(ventas.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("ROLLBACK: el UPDATE y el INSERT se deshacen tras RuntimeException")
    void rollback() throws Exception {
        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":" + productoId + ",\"cantidad\":3,\"simularError\":true}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail", containsString("ROLLBACK")));

        assertThat(productos.findById(productoId).orElseThrow().getStock()).isEqualTo(10);
        assertThat(ventas.count()).isZero();
    }

    @Test
    @DisplayName("No vender cantidades invalidas ni stock insuficiente")
    void validarVenta() throws Exception {
        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":" + productoId + ",\"cantidad\":0}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":" + productoId + ",\"cantidad\":20}"))
                .andExpect(status().isBadRequest());
        assertThat(productos.findById(productoId).orElseThrow().getStock()).isEqualTo(10);
        assertThat(ventas.count()).isZero();
    }

    @Test
    @DisplayName("Reto adicional: filtrar ventas por fecha y nombre")
    void buscarVentas() throws Exception {
        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":" + productoId + ",\"cantidad\":1}"))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/ventas/buscar")
                        .param("inicio", "2020-01-01T00:00:00")
                        .param("fin", "2099-01-01T00:00:00")
                        .param("nombre", "lenovo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
