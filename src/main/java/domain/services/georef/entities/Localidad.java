package domain.services.georef.entities;

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

    public String getNombre() {
        return nombre;
    }

    public Localidad(String nombre, long id) {
        this.nombre = nombre;
        this.id = id;
    }

    public Localidad setId(long id) {
        this.id = id;
        return this;
    }
}
