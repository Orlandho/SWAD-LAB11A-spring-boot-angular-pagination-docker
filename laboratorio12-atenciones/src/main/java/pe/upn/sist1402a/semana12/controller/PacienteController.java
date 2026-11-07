package pe.upn.sist1402a.semana12.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.upn.sist1402a.semana12.model.Paciente;
import pe.upn.sist1402a.semana12.repository.PacienteRepository;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteRepository repoPaciente;

    public PacienteController(PacienteRepository repoPaciente) {
        this.repoPaciente = repoPaciente;
    }

    @GetMapping
    public List<Paciente> listar() {
        return repoPaciente.findAll();
    }

    @PostMapping
    public ResponseEntity<Paciente> registrar(@RequestBody Paciente paciente) {
        if (paciente.getNombre() == null || paciente.getNombre().isBlank() ||
            paciente.getHemoglobina() == null) {
            throw new IllegalArgumentException("Nombre y hemoglobina son obligatorios");
        }
        paciente.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(repoPaciente.save(paciente));
    }
}
