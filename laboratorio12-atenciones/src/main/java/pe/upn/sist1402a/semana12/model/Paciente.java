package pe.upn.sist1402a.semana12.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pacientes")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Double hemoglobina;

    public Paciente() {
    }

    public Paciente(String nombre, Double hemoglobina) {
        this.nombre = nombre;
        this.hemoglobina = hemoglobina;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Double getHemoglobina() { return hemoglobina; }
    public void setHemoglobina(Double hemoglobina) { this.hemoglobina = hemoglobina; }
}
