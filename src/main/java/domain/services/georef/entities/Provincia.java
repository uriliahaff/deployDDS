package domain.services.georef.entities;

import javax.persistence.*;

@Entity //(name = "provincia")
public class Provincia {
    @Id
    @Column(name = "id", columnDefinition = "BIGINT")
    private long id;

    @Column(nullable = false)
    private String nombre;
    // Constructor vacío requerido por Hibernate
    public Provincia() {
    }

    public Provincia(String nombre, long id) {
        this.nombre = nombre;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
    public Provincia setId(long id)
    {
        this.setId(id);
        return this;
    }
}
