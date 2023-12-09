package controllers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import domain.Repositorios.RepositorioEntidad;
import domain.Repositorios.RepositorioServicio;
import domain.Repositorios.RepositorioUsuario;
import domain.Usuarios.Comunidades.Comunidad;
import domain.Usuarios.Comunidades.ConfiguracionNotificacionDeIncidentes;
import domain.Usuarios.Comunidades.Miembro;
import domain.Usuarios.EntidadPrestadora;
import domain.Usuarios.OrganismoDeControl;
import domain.Usuarios.Usuario;
import domain.entidades.Entidad;
import domain.services.notificationSender.ComponenteNotificador;
import domain.services.notificationSender.NotificarViaCorreo;
import domain.services.notificationSender.NotificarViaWpp;
import io.javalin.http.Context;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignInController {
    RepositorioUsuario repositorioUsuario = new RepositorioUsuario();

    RepositorioServicio repositorioServicio = new RepositorioServicio();
    RepositorioEntidad repositorioEntidad = new RepositorioEntidad();

    public void processSignInRedirect(Context context) {
        System.out.println(context.formParam("RegistroType"));
        switch (context.formParam("RegistroType")) {
            case "Miembro":
                processSignInMiembro(context);
                break;
            case "OrganismoDeControl":
                processSignInOrganismoDeControl(context);
                break;
            case "EntidadPrestadora":
                processSignInEntidadPrestadora(context);
                break;
            default:
                context.redirect("/");
                break;
        }
    }

    private Usuario generarUsuario(Context context, String username, String password) {
        boolean usuarioExiste = repositorioUsuario.findUsuarioByUsername(username) != null;
        boolean contrasenaEsSegura = Usuario.contraseñaSegura(password);

        System.out.println("UE: "+ usuarioExiste + "\nCS: "+ contrasenaEsSegura);
        if (usuarioExiste || !contrasenaEsSegura) {
            context.sessionAttribute("error", "Error: El nombre de usuario ya existe o la contraseña no es segura.");
            return null;
        }
        return new Usuario(username, password);
    }
    private void processSignInOrganismoDeControl(Context context)
    {
        String username = context.formParam("username");
        String password = context.formParam("password");

        Usuario usuario = generarUsuario(context, username, password);
        if (usuario == null) {
            context.redirect("/registrarOrganismo");
            return;
        }

        repositorioUsuario.saveUsuario(usuario);
        String nombre = context.formParam("nombre");
        String descripcion = context.formParam("descripcion");
        String correoElectronicoResponsable = context.formParam("correoElectronicoResponsable");

        OrganismoDeControl organismoDeControl = new OrganismoDeControl(
                usuario
                ,correoElectronicoResponsable
                ,nombre
                ,descripcion
        );
        organismoDeControl.setServicio(
                repositorioServicio.findServicioById(
                        Integer.parseInt(context.formParam("servicio"))
                ));
        repositorioUsuario.saveOrganismoDeControl(organismoDeControl);

        context.redirect("/login");
    }
    private void processSignInEntidadPrestadora(Context context)
    {
        String username = context.formParam("username");
        String password = context.formParam("password");

        Usuario usuario = generarUsuario(context, username, password);
        if (usuario == null) {
            context.redirect("/registrarOrganismo");
            return;
        }

        repositorioUsuario.saveUsuario(usuario);
        String nombre = context.formParam("nombre");
        String descripcion = context.formParam("descripcion");
        String correoElectronicoResponsable = context.formParam("correoElectronicoResponsable");
/*
        String nombreEntidad = context.formParam("entidadNombre");
        String emailEntidad = context.formParam("entidadEmail");
        String descripcionEntidad = context.formParam("entidadDescripcion");
        int tipoEntidadId = Integer.parseInt(context.formParam("tipoEntidadId")); // Esto debería ser el ID del tipo de entidad seleccionado

        Entidad entidad = new Entidad(nombreEntidad, repositorioUsuario.findTipoEntidad(tipoEntidadId), emailEntidad, descripcionEntidad);
        repositorioEntidad.save(entidad);

*/
        Entidad entidad = repositorioEntidad.findEntidadById(Integer.parseInt(context.formParam("entidadId")));

        EntidadPrestadora entidadPrestadora = new EntidadPrestadora(
                entidad
                ,usuario
                ,correoElectronicoResponsable
                ,nombre
                ,descripcion
        );

        repositorioUsuario.saveEntidadPrestadora(entidadPrestadora);
        context.redirect("/login");
    }
    private void processSignInMiembro(Context context) {
        String username = context.formParam("username");
        String password = context.formParam("password");

        Usuario usuario = generarUsuario(context, username, password);
        if (usuario == null) {
            context.redirect("/signin");
            return;
        }
        String nombre = context.formParam("nombre");
        String apellido = context.formParam("apellido");
        String correoElectronico = context.formParam("correoElectronico");
        String telefono = context.formParam("telefono");

        if ((correoElectronico == null || correoElectronico.isEmpty()) && (telefono == null || telefono.isEmpty())) {
            context.sessionAttribute("error",  "Por favor, ingresa un correo electrónico o un teléfono.");

            context.redirect("/signin");
            return;
        }

        ComponenteNotificador medioPreferido = (correoElectronico == null || correoElectronico.isEmpty()) ?
                new NotificarViaWpp() : new NotificarViaCorreo();

        Miembro miembro = new Miembro(nombre, apellido, correoElectronico, telefono,
                new ConfiguracionNotificacionDeIncidentes(medioPreferido), usuario);

        repositorioUsuario.saveMiembro(miembro);
        context.redirect("/login");
    }

    public void renderSignInMember(Context context) {
        Map<String, Object> model = new HashMap<>();

        String error = context.sessionAttribute("error");
        if (error != null) {
            model.put("error", error);
            context.sessionAttribute("error", null); // Limpiar el mensaje de error de la sesión
        }

        context.render("signInMiembro.hbs", model);
    }

    public void renderSignInOrganismoDeControl(Context context)
    {
        Map<String, Object> model = new HashMap<>();

        String error = context.sessionAttribute("error");
        if (error != null) {
            model.put("error", error);
            context.sessionAttribute("error", null);
        }
        model.put("servicios", repositorioServicio.findAll());


        context.render("signInOrganismo.hbs", model);
    }

    public void renderSignInEntidadPrestadora(Context context)
    {
        Map<String, Object> model = new HashMap<>();
        String error = context.sessionAttribute("error");
        if (error != null) {
            model.put("error", error);
            context.sessionAttribute("error", null);
        }
        model.put("entidades", repositorioEntidad.findAll());
        context.render("signInEntidad.hbs", model);
    }
}
