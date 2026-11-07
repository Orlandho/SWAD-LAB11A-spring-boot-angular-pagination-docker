package com.ejemplo.laboratorio11.repository;

import com.ejemplo.laboratorio11.model.Venta;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    // Reto adicional: JOIN JPQL y tres named parameters.
    @Query("SELECT v FROM Venta v JOIN v.producto p " +
           "WHERE v.fecha >= :inicio AND v.fecha <= :fin " +
           "AND LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Venta> buscarPorFechaYProducto(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("nombre") String nombre);
}
