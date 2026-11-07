package pe.upn.sist1402a.semana12.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.upn.sist1402a.semana12.model.Atencion;

public interface AtencionRepository extends JpaRepository<Atencion, Long> {

    @Query("SELECT a FROM Atencion a JOIN a.paciente p " +
           "WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) " +
           "AND p.hemoglobina >= :hemoglobinaMin " +
           "AND p.hemoglobina <= :hemoglobinaMax")
    Page<Atencion> buscarAvanzado(
            @Param("nombre") String nombre,
            @Param("hemoglobinaMin") Double hemoglobinaMin,
            @Param("hemoglobinaMax") Double hemoglobinaMax,
            Pageable pageable);
}
