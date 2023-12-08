package domain.services.georef.entities;

import lombok.Getter;
import lombok.Setter;

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

    @ManyToOne
    @JoinColumn(name = "provincia_id")
    @Getter
    @Setter
    private Provincia provincia;

    public Municipio(String nombre, long id, Provincia provincia) {
        this.nombre = nombre;
        this.id = id;
        this.provincia = provincia;
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
