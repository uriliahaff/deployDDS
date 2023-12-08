package domain.services.georef.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class Localidad {
    @Id
    @Column(name = "id", columnDefinition = "BIGINT")
    private long id;

    @Column(nullable = false)
    private String nombre;

    public long getId() {
        return id;
    }

    public Localidad() {
    }

    @ManyToOne
    @JoinColumn(name = "municipio_id")
    @Getter
    @Setter
    private Municipio municipio;

    public String getNombre() {
        return nombre;
    }

    public Localidad(String nombre, long id, Municipio municipio) {
        this.nombre = nombre;
        this.id = id;
        this.municipio = municipio;
    }

    public Localidad setId(long id) {
        this.id = id;
        return this;
    }
}
