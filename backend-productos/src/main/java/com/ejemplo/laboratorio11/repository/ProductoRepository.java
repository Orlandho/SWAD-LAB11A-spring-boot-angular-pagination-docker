package com.ejemplo.laboratorio11.repository;

import com.ejemplo.laboratorio11.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Page<Producto> findByNombreContainingIgnoreCase(
            String nombre,
            Pageable pageable
    );
    // JPQL trabaja con la entidad Producto y sus atributos; Pageable mantiene la paginacion.
    @Query("SELECT p FROM Producto p " +
           "WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) " +
           "AND p.precio >= :precioMin " +
           "AND p.precio <= :precioMax " +
           "AND p.stock >= :stockMin")
    Page<Producto> buscarAvanzado(
            @Param("nombre") String nombre,
            @Param("precioMin") Double precioMin,
            @Param("precioMax") Double precioMax,
            @Param("stockMin") Integer stockMin,
            Pageable pageable);
}
