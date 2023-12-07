package domain.services.notificationSender;
import domain.Usuarios.Comunidades.Miembro;
import domain.services.wppSender.TwiloAdapter;
import domain.services.wppSender.WwpSender;

public class NotificarViaWpp implements ComponenteNotificador{
private WwpSender wwpSender;
    public NotificarViaWpp() {
        this.wwpSender = new TwiloAdapter();
    }
    @Override
    public void enviarNotificacion(String titulo, Miembro destinatario, String message) {
        wwpSender.enviarWpp(titulo, destinatario.getTelefono(), message);
    }

    @Override
    public String name() {
        return "Whatsapp";
    }
}
