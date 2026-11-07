package pe.upn.sist1402a.semana12.controller;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.upn.sist1402a.semana12.dto.AtencionRequest;
import pe.upn.sist1402a.semana12.model.Atencion;
import pe.upn.sist1402a.semana12.model.HistorialClinico;
import pe.upn.sist1402a.semana12.service.AtencionService;

@RestController
@RequestMapping("/api")
public class AtencionController {

    private final AtencionService service;

    public AtencionController(AtencionService service) {
        this.service = service;
    }

    @PostMapping("/atenciones")
    public ResponseEntity<Atencion> registrar(@Valid @RequestBody AtencionRequest request)
            throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.registrarAtencion(request));
    }

    @GetMapping("/atenciones")
    public List<Atencion> listar() {
        return service.listarAtenciones();
    }

    @GetMapping("/historiales")
    public List<HistorialClinico> historiales() {
        return service.listarHistoriales();
    }

    @GetMapping("/atenciones/buscar")
    public Page<Atencion> buscar(
            @RequestParam(defaultValue = "") String nombre,
            @RequestParam(defaultValue = "0") Double hemoglobinaMin,
            @RequestParam(defaultValue = "30") Double hemoglobinaMax,
            @PageableDefault(size = 5, sort = "id") Pageable pageable) {
        return service.buscarAvanzado(nombre, hemoglobinaMin, hemoglobinaMax, pageable);
    }
}
