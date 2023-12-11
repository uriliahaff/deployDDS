package domain.Usuarios;


import domain.entidades.Entidad;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "entidad_prestadora")
public class EntidadPrestadora{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "entidad_id", nullable = false)
    @Getter
    @Setter
    private Entidad entidad;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "usuario_id", nullable = false)
    @Getter
    @Setter
    private Usuario usuario;

    @Column(name = "correo_electronico_responsable", nullable = false)
    @Getter
    @Setter
    private String correoElectronicoResponsable;

    @Column(name = "nombre", nullable = false)
    @Getter
    @Setter
    private String nombre;

    @Column(name="descripcion", columnDefinition="TEXT")

    @Getter
    @Setter
    private String descripcion;

    public EntidadPrestadora() {
    }

    public EntidadPrestadora(Entidad entidad, Usuario usuario, String correoElectronicoResponsable, String nombre, String descripcion) {
        this.entidad = entidad;
        this.usuario = usuario;
        this.correoElectronicoResponsable = correoElectronicoResponsable;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Métodos "getters"
    public int getId() {
        return id;
    }

    public Entidad getEntidad() {
        return entidad;
    }


    public String getCorreoElectronicoResponsable() {
        return correoElectronicoResponsable;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}