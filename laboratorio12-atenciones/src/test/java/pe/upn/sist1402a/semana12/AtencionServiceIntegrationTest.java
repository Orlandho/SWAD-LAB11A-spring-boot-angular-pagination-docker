package pe.upn.sist1402a.semana12;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pe.upn.sist1402a.semana12.dto.AtencionRequest;
import pe.upn.sist1402a.semana12.model.Atencion;
import pe.upn.sist1402a.semana12.model.Paciente;
import pe.upn.sist1402a.semana12.repository.AtencionRepository;
import pe.upn.sist1402a.semana12.repository.HistorialRepository;
import pe.upn.sist1402a.semana12.repository.PacienteRepository;
import pe.upn.sist1402a.semana12.service.AtencionService;

@SpringBootTest
class AtencionServiceIntegrationTest {

    @Autowired private AtencionService service;
    @Autowired private PacienteRepository pacientes;
    @Autowired private AtencionRepository atenciones;
    @Autowired private HistorialRepository historiales;

    private Long pacienteId;

    @BeforeEach
    void preparar() {
        historiales.deleteAll();
        atenciones.deleteAll();
        pacientes.deleteAll();
        pacienteId = pacientes.save(new Paciente("Maria Lopez", 11.2)).getId();
    }

    @Test
    void registroExitosoGuardaAtencionEHistorial() throws Exception {
        Atencion a = service.registrarAtencion(
                new AtencionRequest(pacienteId, "Control de anemia", false));

        assertNotNull(a.getId());
        assertEquals(1, atenciones.count());
        assertEquals(1, historiales.count());
        assertEquals(a.getId(), historiales.findAll().get(0).getAtencion().getId());
    }

    @Test
    void errorControladoRevierteLasDosTablas() {
        Exception error = assertThrows(Exception.class, () ->
                service.registrarAtencion(
                        new AtencionRequest(pacienteId, "Control de anemia", true)));

        assertTrue(error.getMessage().contains("rollback"));
        assertEquals(0, atenciones.count(), "No debe persistir la atencion");
        assertEquals(0, historiales.count(), "No debe persistir el historial");
        assertEquals(1, pacientes.count(), "Paciente previo no debe eliminarse");
    }

    @Test
    void busquedaJpqlConNamedParametersFunciona() throws Exception {
        service.registrarAtencion(
                new AtencionRequest(pacienteId, "Control de anemia", false));
        Page<Atencion> encontrados =
                service.buscarAvanzado("maria", 10.0, 12.0, PageRequest.of(0, 5));
        assertEquals(1, encontrados.getTotalElements());
        assertEquals("Maria Lopez", encontrados.getContent().get(0).getPaciente().getNombre());

        Page<Atencion> vacios =
                service.buscarAvanzado("maria", 13.0, 15.0, PageRequest.of(0, 5));
        assertTrue(vacios.isEmpty());
    }
}
