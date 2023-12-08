package controllers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import domain.Repositorios.RepositorioDireccion;
import domain.Repositorios.RepositorioServicio;
import domain.Repositorios.RepositorioUsuario;
import domain.Usuarios.Comunidades.Comunidad;
import domain.Usuarios.Comunidades.ConfiguracionNotificacionDeIncidentes;
import domain.Usuarios.Comunidades.Miembro;
import domain.Usuarios.EntidadPrestadora;
import domain.Usuarios.OrganismoDeControl;
import domain.Usuarios.Rol;
import domain.Usuarios.Usuario;
import domain.localizaciones.Direccion;
import domain.services.NavBarVisualizer;
import domain.services.georef.entities.Localidad;
import domain.services.georef.entities.Municipio;
import domain.services.georef.entities.Provincia;
import domain.services.notificationSender.ComponenteNotificador;
import domain.services.notificationSender.NotificarViaCorreo;
import domain.services.notificationSender.NotificarViaWpp;
import domain.servicios.Servicio;
import io.javalin.http.Context;
import sever.Server;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PerfilController
{
    RepositorioUsuario repositorioUsuario = new RepositorioUsuario();

    RepositorioDireccion repositorioDireccion = new RepositorioDireccion();

    RepositorioServicio repositorioServicio = new RepositorioServicio();
    public void redirectPerfilPropio(Context context)
    {

        int profileUserId = Integer.parseInt(context.cookie("id"));
        Usuario user = repositorioUsuario.findUsuarioById(profileUserId);

        OrganismoDeControl organismoDeControl = repositorioUsuario.findOrganismoDeControlByUserId(profileUserId);
        if(organismoDeControl!=null)
        {
            //context.redirect("/perfil/organismo/"+organismoDeControl.getId());
            return;
        }
        EntidadPrestadora entidadPrestadora = repositorioUsuario.findEntidadPrestadoraByUserId(profileUserId);
        if (entidadPrestadora!= null)
        {
            //context.redirect("/perfil/entidad/"+entidadPrestadora.getId());
            return;
        }
        Miembro miembro = repositorioUsuario.findMiembroByUsuarioId(profileUserId);
        //nuevoRenderPerfilMiembros(context,miembro, user);


    }

   public void perfilPropio(Context context){
       Map<String, Object> model = new HashMap<>();
       int profileUserId = Integer.parseInt(context.cookie("id"));
       model.put("userID",profileUserId);
       Usuario user = repositorioUsuario.findUsuarioById(profileUserId);

       List<OrganismoDeControl> organismosDeControl = repositorioUsuario.findOrganismosDeControlByUserId(profileUserId);

       if(!organismosDeControl.isEmpty())
       {
           model.put("esOrganismoControl",true);
           model.put("organismosDeControl",organismosDeControl);
       }

       List<EntidadPrestadora> entidadPrestadoras = repositorioUsuario.findEntidadesPrestadoraslByUserId(profileUserId);

       if(!entidadPrestadoras.isEmpty())
       {
           model.put("esEntidadPrestadora",true);
           model.put("entidadesPrestadoras",entidadPrestadoras);
           model.put("provincias",repositorioDireccion.findAllProvincias());
           model.put("localidades",repositorioDireccion.findAllLocalidades());
           model.put("municipios",repositorioDireccion.findAllMunicipios());
       }


       Miembro miembro = repositorioUsuario.findMiembroByUsuarioId(profileUserId);
       if(miembro!=null){
           model.put("esMiembro",true);
           model.put("emailMiembro",miembro.getCorreoElectronico());
           model.put("telefonoMiembro",miembro.getTelefono());
           model.put("showMedioPreferido",miembro.getConfiguracionNotificacionDeIncidentes().getMedioPreferido().name());
           model.put("miembro",miembro);
           List<Servicio> servicios = repositorioServicio.findAll();
           for (Servicio servicioUsuario : miembro.getServiciosQueAfectan()) {
               servicios.removeIf(servicio -> servicio.getId() == servicioUsuario.getId());
           }
           model.put("listaServiciosNoAfectan",servicios);
           model.put("configuracionNotificacionDeIncidentes", miembro.getConfiguracionNotificacionDeIncidentes());

       }

       model.put("username",user.getUsername());
       NavBarVisualizer navBarVisualizer = new NavBarVisualizer();
       navBarVisualizer.colocarItems(user.getRoles(), model);

       context.render("perfilPropio.hbs", model);
   }
    private void nuevoRenderPerfilMiembros(Context context, Miembro miembro, Usuario user)
    {
        Map<String, Object> model = new HashMap<>();

        if(miembro.getUsuario() == user){
            model.put("editarMiembro",true);
        }

        model.put("profileId",user.getId());
        model.put("username", context.cookie("username"));
        miembro.getServiciosQueAfectan().size();
        model.put("miembro",miembro);
        model.put("showMedioPreferido", miembro.getConfiguracionNotificacionDeIncidentes().getMedioPreferido().name());

        model.put("configuracionNotificacionDeIncidentes", miembro.getConfiguracionNotificacionDeIncidentes());

        model.put("provincias",repositorioDireccion.findAllProvincias());
        model.put("localidades",repositorioDireccion.findAllLocalidades());
        model.put("municipios",repositorioDireccion.findAllMunicipios());


        List<Servicio> servicios = repositorioServicio.findAll();
        for (Servicio servicioUsuario : miembro.getServiciosQueAfectan()) {
            servicios.removeIf(servicio -> servicio.getId() == servicioUsuario.getId());
        }
        model.put("listaServiciosNoAfectan",servicios);

        NavBarVisualizer navBarVisualizer = new NavBarVisualizer();
        navBarVisualizer.colocarItems(user.getRoles(), model);

        context.render("perfilPropio.hbs", model);
    }



    public void addLugarDeInteres(Context context)
    {
        int userId = Integer.parseInt(context.cookie("id"));
        Miembro miembro = repositorioUsuario.findMiembroByUsuarioId(userId);
        String provinciaIdRaw = context.formParam("provincia");
        String localidadIdRaw = context.formParam("localidad");
        String municipioIdRaw = context.formParam("municipio");

        Provincia provincia = repositorioDireccion.findProvincia(Integer.parseInt(provinciaIdRaw));
        Direccion direccion;
        if(municipioIdRaw.equals(""))
        {
            direccion = new Direccion(provincia);
        }
        else
        {
            Municipio municipio = repositorioDireccion.findMunicipio(Integer.parseInt(municipioIdRaw));
            if(localidadIdRaw.equals(""))
            {
                direccion = new Direccion(provincia, municipio);
            }
            else
            {
                Localidad localidad = repositorioDireccion.findLocalidad(Integer.parseInt(localidadIdRaw));
                direccion = new Direccion(provincia,municipio,localidad);
            }
        }
        repositorioDireccion.saveDireccion(direccion);
        miembro.addLugarDeInteres(direccion);
        repositorioUsuario.updateMiembro(miembro);

        context.redirect("/perfil");
    }

    public void addServicioDeInteres(Context context)
    {
        int miembroId = Integer.parseInt(context.pathParam("id")); // Asume que el ID del miembro está en la ruta
        int servicioId = Integer.parseInt(context.formParam("servicioId"));

        Miembro miembro = repositorioUsuario.findMiembroByUsuarioId(miembroId);
        Servicio servicio = repositorioServicio.findServicioById(servicioId);
        miembro.addServicioDeInteres(servicio);
        repositorioUsuario.updateMiembro(miembro);
        context.redirect("/perfil");
    }

    public void borrarServicio(Context context)
    {
        int miembroId = Integer.parseInt(context.pathParam("id")); // Asume que el ID del miembro está en la ruta
        int servicioId = Integer.parseInt(context.pathParam("idServicio"));

        Miembro miembro = repositorioUsuario.findMiembroByUsuarioId(miembroId);
        Servicio servicio = repositorioServicio.findServicioById(servicioId);
        miembro.removeServicioDeInteres(servicio);
        repositorioUsuario.updateMiembro(miembro);
        context.redirect("/perfil");
    }


    public void agregarHorario(Context context) {
        try {
            int miembroId = Integer.parseInt(context.pathParam("idMiembro"));
            String horaString = context.formParam("nuevaHora"); // "HH:MM"

            String[] partes = horaString.split(":");
            int horas = Integer.parseInt(partes[0]);
            int minutos = Integer.parseInt(partes[1]);
            int totalMinutos = horas * 60 + minutos;

            Miembro miembro = repositorioUsuario.findMiembroByUsuarioId(miembroId);
            if (miembro != null) {
                ConfiguracionNotificacionDeIncidentes configuracion = miembro.getConfiguracionNotificacionDeIncidentes();
                configuracion.getHorarioPreferencia().add((float)totalMinutos);

                repositorioUsuario.updateMiembro(miembro);

            }

            context.redirect("/perfil");
        } catch (NumberFormatException e) {
            context.status(400).result("Formato de hora no válido.");
        } catch (Exception e) {
            context.status(500).result("Error al procesar la solicitud: " + e.getMessage());
        }
    }

    public void borrarHorario(Context context) {
        try {
            int miembroId = Integer.parseInt(context.pathParam("idMiembro"));
            float horarioABorrar = Float.parseFloat(context.formParam("horario"));

            Miembro miembro = repositorioUsuario.findMiembroByUsuarioId(miembroId);
            if (miembro != null) {
                ConfiguracionNotificacionDeIncidentes configuracion = miembro.getConfiguracionNotificacionDeIncidentes();
                configuracion.getHorarioPreferencia().remove(horarioABorrar);

                repositorioUsuario.updateMiembro(miembro);
            }

            context.redirect("/perfil" );
        } catch (NumberFormatException e) {
            context.status(400).result("Formato de horario no válido.");
        } catch (Exception e) {
            context.status(500).result("Error al procesar la solicitud: " + e.getMessage());
        }
    }

    public void guardarMedioPreferido(Context context)
    {

        String medioPreferido = context.formParam("preferencia");
        int miembroId = Integer.parseInt(context.pathParam("idMiembro"));
        Miembro miembro = repositorioUsuario.findMiembroByUsuarioId(miembroId);


        // Actualizar el medio preferido
        if ("email".equals(medioPreferido)) {
            miembro.getConfiguracionNotificacionDeIncidentes().setMedioPreferido(new NotificarViaCorreo());
            // Configurar para notificaciones por correo electrónico
        } else if ("telefono".equals(medioPreferido))
        {
            miembro.getConfiguracionNotificacionDeIncidentes().setMedioPreferido(new NotificarViaWpp());

        }
        repositorioUsuario.updateMiembro(miembro);

        repositorioUsuario.updateMiembro(miembro);
        context.redirect("/perfil"); // Redirigir al usuario
    }

}
