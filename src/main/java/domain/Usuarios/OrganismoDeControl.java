package domain.Usuarios;

import domain.entidades.Entidad;
import domain.servicios.Servicio;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "organismo_de_control")
public class OrganismoDeControl{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "servicio_id")
    @Setter
    private Servicio servicio;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "usuario_id", nullable = false)
    @Setter
    private Usuario usuario;

    @Column(name = "correoElectronicoResponsable",nullable = false)
    @Setter
    private String correoElectronicoResponsable;

    @Column(name = "nombre",nullable = false)
    @Setter
    private String nombre;

    @Column(name="descripcion", columnDefinition="TEXT")

    @Setter
    private String descripcion;



    public int getId() {
        return id;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public OrganismoDeControl(/*Usuario user,*/String username, String password , String correoElectronicoResponsable, String nombre, String descripcion) {
        this.usuario = new Usuario(username,password);
        //this.usuario = usuario;
        this.correoElectronicoResponsable = correoElectronicoResponsable;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    public OrganismoDeControl(Usuario user, String correoElectronicoResponsable, String nombre, String descripcion) {
        this.usuario = user;
        this.correoElectronicoResponsable = correoElectronicoResponsable;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public OrganismoDeControl() {
    }
    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public Usuario getUsuario() {
        return usuario;
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
