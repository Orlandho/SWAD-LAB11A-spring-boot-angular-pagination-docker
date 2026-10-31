package com.ejemplo.laboratorio11;

import com.ejemplo.laboratorio11.model.Producto;
import com.ejemplo.laboratorio11.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
                new Producto(null, "UPS 1000VA", 520.0, 3)
        ));
    }

    @Test
    @DisplayName("GET /api/productos/pagina retorna 5 elementos por defecto, first=true, last=false")
    void testGetPaginaDefault() throws Exception {
        mockMvc.perform(get("/api/productos/pagina")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.size", is(5)))
                .andExpect(jsonPath("$.totalElements", is(7)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.last", is(false)))
                .andExpect(jsonPath("$.content[0].estadoStock", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/productos/pagina?page=1&size=5 retorna la segunda pagina con 2 elementos y last=true")
    void testGetSegundaPagina() throws Exception {
        mockMvc.perform(get("/api/productos/pagina")
                        .param("page", "1")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.number", is(1)))
                .andExpect(jsonPath("$.first", is(false)))
                .andExpect(jsonPath("$.last", is(true)));
    }

    @Test
    @DisplayName("GET /api/productos/pagina?sort=precio,desc ordena los productos por precio descendente")
    void testGetPaginaOrdenadaPorPrecioDesc() throws Exception {
        mockMvc.perform(get("/api/productos/pagina")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "precio,desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombre", is("Laptop Lenovo")))
                .andExpect(jsonPath("$.content[0].precio", is(2500.0)));
    }

    @Test
    @DisplayName("GET /api/productos/pagina/buscar?nombre=teclado filtra correctamente y retorna metadatos")
    void testBuscarPorNombre() throws Exception {
        mockMvc.perform(get("/api/productos/pagina/buscar")
                        .param("nombre", "teclado")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nombre", is("Teclado mecanico")))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }

    @Test
    @DisplayName("GET /api/productos/pagina/buscar con nombre vacio retorna todos paginados")
    void testBuscarConNombreVacio() throws Exception {
        mockMvc.perform(get("/api/productos/pagina/buscar")
                        .param("nombre", "")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.totalElements", is(7)));
    }

    @Test
    @DisplayName("GET /api/productos/pagina?page=-1 aplica tolerancia de Spring Data y retorna primera pagina (number=0)")
    void testGetPaginaIndiceNegativoAplicaFallback() throws Exception {
        mockMvc.perform(get("/api/productos/pagina")
                        .param("page", "-1")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.content", hasSize(5)));
    }

    @Test
    @DisplayName("GET /api/productos/pagina?size=0 aplica tolerancia de Spring Data y retorna tamanio por defecto")
    void testGetPaginaTamanioCeroAplicaFallback() throws Exception {
        mockMvc.perform(get("/api/productos/pagina")
                        .param("page", "0")
                        .param("size", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size", is(5)))
                .andExpect(jsonPath("$.content", hasSize(5)));
    }

    @Test
    @DisplayName("GET /api/productos/pagina?page=invalido aplica tolerancia y retorna primera pagina")
    void testGetPaginaTipoInvalidoAplicaFallback() throws Exception {
        mockMvc.perform(get("/api/productos/pagina")
                        .param("page", "invalido")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number", is(0)));
    }
}

