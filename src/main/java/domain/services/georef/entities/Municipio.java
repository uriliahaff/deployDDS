package domain.services.georef.entities;

import javax.persistence.*;

@Entity //(name = "municipio")
public class Municipio {
    @Id
    @Column(name = "id", columnDefinition = "BIGINT")
    private long id;

    public long getId() {
        return id;
    }

    @Column(nullable = false)
    private String nombre;

    private

    public Municipio(String nombre, long id) {
        this.nombre = nombre;
        this.id = id;
    }



    public String getNombre() {
        return nombre;
    }

    public Municipio() {
    }

    public Municipio setId(long id)
    {
        this.id = id;
        return this;
    }
}
