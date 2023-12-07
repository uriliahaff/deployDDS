package domain.services.notificationSender;

import domain.Usuarios.Comunidades.Miembro;
import domain.services.emailSender.EmailSender;
import domain.services.emailSender.EmailSenderAdapter;

public class NotificarViaCorreo implements ComponenteNotificador{
private EmailSender emailSender;
public NotificarViaCorreo()
{
    this.emailSender = new EmailSenderAdapter();
}
    @Override
    public void enviarNotificacion(String titulo, Miembro destinatario, String message) {
        //emailSender = new EmailSenderAdapter();
        System.out.println("enviando mail a "+ destinatario.getNombre() );
        this.emailSender.enviarMail(titulo, destinatario.getCorreoElectronico(), message);
    }

    @Override
    public String name() {
        return "Correo Electronico";
    }

}
