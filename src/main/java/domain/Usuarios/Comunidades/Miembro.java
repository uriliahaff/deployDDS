package domain.Usuarios.Comunidades;

import domain.Usuarios.Usuario;
import domain.informes.Incidente;
import domain.localizaciones.Direccion;
import domain.services.notificadorDeIncidentes.CommandoNotificacion;
import domain.services.notificationSender.NotificarViaCorreo;
import domain.services.notificationSender.NotificarViaWpp;
import domain.servicios.Servicio;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "miembro")
public class Miembro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Getter
    @Setter
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Getter
    @Setter
    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "correoElectronico", nullable = false)
    private String correoElectronico;

    public Usuario getUsuario() {
        return usuario;
    }

    @Column(name = "telefono", nullable = false)

    private String telefono;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "configuracion_id")
    private ConfiguracionNotificacionDeIncidentes configuracionNotificacionDeIncidentes;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(
            name = "miembro_servicio",
            joinColumns = @JoinColumn(name = "miembro_id"),
            inverseJoinColumns = @JoinColumn(name = "servicio_id")
    )
    private List<Servicio> serviciosQueAfectan = new ArrayList<>();
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "miembro_comunidad",
            joinColumns = @JoinColumn(name = "miembro_id"),
            inverseJoinColumns = @JoinColumn(name = "comunidad_id")
    )
    private List<Comunidad> comunidades = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(
            name = "miembro_direccion",
            joinColumns = @JoinColumn(name = "miembro_id"),
            inverseJoinColumns = @JoinColumn(name = "direccion_id")
    )
    private List<Direccion> lugaresDeInteres = new ArrayList<>();

    public List<Servicio> getServiciosQueAfectan() {
        return serviciosQueAfectan;
    }

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "usuario_id")
    @Getter
    @Setter
    private Usuario usuario;

    // Constructor vacío para Hibernate
    public Miembro() {
    }

    // Constructor completo
    public Miembro(String nombre, String apellido, String correoElectronico, String telefono,
                   ConfiguracionNotificacionDeIncidentes configuracionNotificacionDeIncidentes,
                   Usuario usuario) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correoElectronico = correoElectronico;
        this.telefono = telefono;
        this.configuracionNotificacionDeIncidentes = configuracionNotificacionDeIncidentes;
        this.usuario = usuario;
    }

    public List<Comunidad> getComunidades() {
        return comunidades;
    }
    public void addLugarDeInteres(Direccion direccion)
    {
        lugaresDeInteres.add(direccion);
    }
    public void removeLugarDeInteres(Direccion direccion)
    {
        lugaresDeInteres.remove(direccion);
    }
    public void addServicioDeInteres(Servicio servicio)
    {
        serviciosQueAfectan.add(servicio);
    }
    public void removeServicioDeInteres(Servicio servicio)
    {
        serviciosQueAfectan.remove(servicio);
    }
//TODO: Servicio de interes, entidad de interes,

    public boolean estaEnArea(String Loc)
    {
        return true;
    }

    public List<Direccion> getLugaresDeInteres()
    {
        return this.lugaresDeInteres;
    }
    public boolean lugarDeInteres(Direccion lugar)
    {
        return lugaresDeInteres.stream().anyMatch(l -> l.sameLocation(lugar));
    }

    public CommandoNotificacion getCommandoNotificar(Incidente incidente)
    {
        if (this.configuracionNotificacionDeIncidentes == null)
            this.configuracionNotificacionDeIncidentes = new ConfiguracionNotificacionDeIncidentes(new NotificarViaCorreo());
        return configuracionNotificacionDeIncidentes.generarComando(this,incidente);
        //TODO: Aca tiene que hacer toda la cosa con el comand y no se que
    }

    public boolean leAfecta(Servicio servicio)
    {
        return serviciosQueAfectan.contains(servicio);
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public ConfiguracionNotificacionDeIncidentes getConfiguracionNotificacionDeIncidentes() {
        return configuracionNotificacionDeIncidentes;
    }

    public String getTelefono() {return telefono;}

    public void addComunidad(Comunidad comunidad)
    {
        this.comunidades.add(comunidad);
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void removeComunidad(Comunidad comunidad) {   this.comunidades.remove(comunidad); }
}
