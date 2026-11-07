package pe.upn.sist1402a.semana12.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AtencionRequest {

    @NotNull(message = "El pacienteId es obligatorio")
    private Long pacienteId;

    @NotBlank(message = "El diagnostico es obligatorio")
    private String diagnostico;

    // Solo se utiliza para demostrar rollback en el laboratorio.
    private Boolean simularError = false;

    public AtencionRequest() {
    }

    public AtencionRequest(Long pacienteId, String diagnostico, Boolean simularError) {
        this.pacienteId = pacienteId;
        this.diagnostico = diagnostico;
        this.simularError = simularError;
    }

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    public Boolean getSimularError() { return simularError; }
    public void setSimularError(Boolean simularError) { this.simularError = simularError; }
}
