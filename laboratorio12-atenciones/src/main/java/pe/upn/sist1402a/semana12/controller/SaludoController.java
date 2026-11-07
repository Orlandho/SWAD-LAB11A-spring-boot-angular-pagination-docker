package pe.upn.sist1402a.semana12.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SaludoController {

    @GetMapping("/api/saludo")
    public String saludo() {
        return "Backend activo";
    }
}
