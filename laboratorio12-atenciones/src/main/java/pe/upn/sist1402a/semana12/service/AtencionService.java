package pe.upn.sist1402a.semana12.service;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.upn.sist1402a.semana12.dto.AtencionRequest;
import pe.upn.sist1402a.semana12.model.Atencion;
import pe.upn.sist1402a.semana12.model.HistorialClinico;
import pe.upn.sist1402a.semana12.model.Paciente;
import pe.upn.sist1402a.semana12.repository.AtencionRepository;
import pe.upn.sist1402a.semana12.repository.HistorialRepository;
import pe.upn.sist1402a.semana12.repository.PacienteRepository;

@Service
public class AtencionService {

    private final PacienteRepository repoPaciente;
    private final AtencionRepository repoAtencion;
    private final HistorialRepository repoHistorial;

    public AtencionService(PacienteRepository repoPaciente,
                           AtencionRepository repoAtencion,
                           HistorialRepository repoHistorial) {
        this.repoPaciente = repoPaciente;
        this.repoAtencion = repoAtencion;
        this.repoHistorial = repoHistorial;
    }

    // El limite transaccional debe estar en Service, nunca en Controller.
    @Transactional(rollbackFor = Exception.class)
    public Atencion registrarAtencion(AtencionRequest request) throws Exception {
        Paciente paciente = repoPaciente.findById(request.getPacienteId())
                .orElseThrow(() -> new NoSuchElementException("Paciente no encontrado"));
        if (request.getDiagnostico() == null || request.getDiagnostico().isBlank()) {
            throw new IllegalArgumentException("El diagnostico no puede estar vacio");
        }

        Atencion atencion = new Atencion(paciente, request.getDiagnostico());
        repoAtencion.saveAndFlush(atencion);  // Operacion A: INSERT atencion

        HistorialClinico historial = atencion.crearHistorial();
        repoHistorial.saveAndFlush(historial); // Operacion B: INSERT historial

        // Fallo comprobable incluso para una excepcion checked.
        // rollbackFor=Exception.class revierte ambos INSERT ya enviados a H2.
        if (Boolean.TRUE.equals(request.getSimularError())) {
            throw new Exception("Fallo controlado: rollback de atencion e historial");
        }
        return atencion;
    }

    public List<Atencion> listarAtenciones() {
        return repoAtencion.findAll();
    }

    public List<HistorialClinico> listarHistoriales() {
        return repoHistorial.findAll();
    }

    public Page<Atencion> buscarAvanzado(String nombre,
                                         Double hemoglobinaMin,
                                         Double hemoglobinaMax,
                                         Pageable pageable) {
        if (hemoglobinaMin > hemoglobinaMax) {
            throw new IllegalArgumentException("El minimo no puede superar al maximo");
        }
        return repoAtencion.buscarAvanzado(
                nombre, hemoglobinaMin, hemoglobinaMax, pageable);
    }
}
