package domain.entidades;

import domain.localizaciones.Direccion;
import domain.services.georef.entities.Localidad;
import domain.services.georef.entities.Municipio;
import domain.services.georef.entities.Provincia;
import domain.servicios.PrestacionDeServicio;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "establecimiento")
public class Establecimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    public int getId() {
        return id;
    }

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name="descripcion", columnDefinition="TEXT")

    private String descripcion;

    @OneToMany(mappedBy = "establecimiento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PrestacionDeServicio> servicios;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entidad_id")
    private Entidad entidad;



    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "direccion_id")
    private Direccion direccion;

    public List<PrestacionDeServicio> getServicios() {
        return servicios;
    }

    public Establecimiento(){}
    public Establecimiento(String nombre,String descripcion,Provincia provincia,Localidad localidad,Municipio municipio){
        this.nombre= nombre;
        this.descripcion=descripcion;

        this.direccion = new Direccion(provincia, municipio, localidad);
        this.servicios = new ArrayList<>();

    }
    public Establecimiento(String nombre,String descripcion, Direccion direccion){
        this.nombre= nombre;
        this.descripcion=descripcion;

        this.direccion = direccion;
        this.servicios = new ArrayList<>();

    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public Entidad getEntidad() {
        return this.entidad;
    }
    public void setEntidad(Entidad entidad) {
        this.entidad = entidad;
    }
    public void agregarServicio(PrestacionDeServicio servicio) {
        servicios.add(servicio); // Agregar un servicio a la lista de servicios

    }
}
