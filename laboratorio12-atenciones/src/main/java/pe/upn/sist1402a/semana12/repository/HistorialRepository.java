package pe.upn.sist1402a.semana12.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.upn.sist1402a.semana12.model.HistorialClinico;

public interface HistorialRepository extends JpaRepository<HistorialClinico, Long> {
}
