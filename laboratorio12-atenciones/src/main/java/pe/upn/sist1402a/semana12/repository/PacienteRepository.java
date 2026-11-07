package pe.upn.sist1402a.semana12.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.upn.sist1402a.semana12.model.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
}
