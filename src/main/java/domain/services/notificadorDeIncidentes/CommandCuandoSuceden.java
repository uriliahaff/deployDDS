package domain.services.notificadorDeIncidentes;

import domain.Usuarios.Comunidades.Miembro;
import domain.informes.Incidente;
import domain.services.notificationSender.EnviadorDeNotificaciones;

public class CommandCuandoSuceden extends CommandoNotificacion{
    public CommandCuandoSuceden(Miembro miembro, Incidente incidente) {
        super(miembro, incidente);
    }

    @Override
    public void notificarIncidente() {
        EnviadorDeNotificaciones enviadorDeNotificaciones = EnviadorDeNotificaciones.getInstance();
        enviadorDeNotificaciones.cambiarEstrategia(miembro.getConfiguracionNotificacionDeIncidentes().getMedioPreferido());
        enviadorDeNotificaciones.enviarNotificacion("Nuevo Incidente", miembro,"Incidente en "+incidente.getServicioAfectado());
    }
}
