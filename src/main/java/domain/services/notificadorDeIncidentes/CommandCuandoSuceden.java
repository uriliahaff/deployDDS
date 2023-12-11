package domain.services.notificadorDeIncidentes;

import domain.Usuarios.Comunidades.Miembro;
import domain.informes.Incidente;
import domain.services.notificationSender.EnviadorDeNotificaciones;

public class CommandCuandoSuceden extends CommandoNotificacion{
    public CommandCuandoSuceden(Miembro miembro, Incidente incidente) {
        super(miembro, incidente);
    }
    private static EnviadorDeNotificaciones enviadorDeNotificaciones = EnviadorDeNotificaciones.getInstance();

    @Override
    public void notificarIncidente() {
        //enviadorDeNotificaciones.cambiarEstrategia(miembro.getConfiguracionNotificacionDeIncidentes().getMedioPreferido());
        enviadorDeNotificaciones.enviarNotificacion(
                miembro.getConfiguracionNotificacionDeIncidentes().getMedioPreferido()
                ,"Nuevo Incidente"
                , miembro
                ,"Ups! Nuevo incidente de "+incidente.getServicioAfectado().getServicio().getNombre()
                        +" - "
                        +incidente.getServicioAfectado().getEstablecimiento().getEntidad().getNombre()
                        +" - "
                        +incidente.getServicioAfectado().getEstablecimiento().getDireccion().getLocalidad().getNombre()
                        +" ("
                        +incidente.getServicioAfectado().getEstablecimiento().getDireccion().getMunicipio().getNombre()
                        +", "
                        +incidente.getServicioAfectado().getEstablecimiento().getDireccion().getProvincia().getNombre()
                        +"): "
                        +incidente.getDescripcion()
        );
    }
}
