package pe.upn.sist1402a.semana12.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "historiales")
public class HistorialClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "atencion_id", nullable = false)
    private Atencion atencion;

    @Column(nullable = false)
    private String descripcion;

    public HistorialClinico() {
    }

    public HistorialClinico(Atencion atencion, String descripcion) {
        this.atencion = atencion;
        this.descripcion = descripcion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Atencion getAtencion() { return atencion; }
    public void setAtencion(Atencion atencion) { this.atencion = atencion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
