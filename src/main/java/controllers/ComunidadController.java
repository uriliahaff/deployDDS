package controllers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import domain.Repositorios.RepositorioComunidad;
import domain.Repositorios.RepositorioServicio;
import domain.Repositorios.RepositorioUsuario;
import domain.Usuarios.Comunidades.Comunidad;
import domain.Usuarios.Comunidades.Miembro;
import domain.Usuarios.EntidadPrestadora;
import domain.Usuarios.Rol;
import domain.Usuarios.Usuario;
import domain.services.NavBarVisualizer;
import domain.servicios.Servicio;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import sever.Server;

import java.util.*;


import java.io.IOException;
import java.util.stream.Collectors;

public class ComunidadController {
    private static RepositorioComunidad repositorioComunidad = new RepositorioComunidad();
    private static RepositorioUsuario repositorioUsuario = new RepositorioUsuario();
    private static RepositorioServicio repositorioServicio = new RepositorioServicio();

    public void indexComunidades(Context context) {
        Map<String, Object> model = new HashMap<>();
        model.put("username", context.cookie("username"));
        Usuario user = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));
        model.put("UserId",context.cookie("id"));

        List<Comunidad> comunidades = repositorioComunidad.findAll();
        model.put("comunidades", comunidades);
        model.put("editarComunidad", user.tienePermiso("editarComunidad")); // RolX es el rol necesario

        List<Servicio> intereses = repositorioServicio.findAll();
        model.put("intereses", intereses);

        NavBarVisualizer navBarVisualizer = new NavBarVisualizer();
        navBarVisualizer.colocarItems(user.getRoles(), model);

        context.render("comunidades.hbs", model);
    }

    public void indexCreacion(Context context) {
        Map<String, Object> model = new HashMap<>();
        model.put("username", context.cookie("username"));
        Usuario user = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));
        model.put("UserId",context.cookie("id"));

        List<Comunidad> comunidades = repositorioComunidad.findAll();
        model.put("comunidades", comunidades);
        model.put("editarComunidad", user.tienePermiso("editarComunidad")); // RolX es el rol necesario

        List<Servicio> intereses = repositorioServicio.findAll();
        model.put("intereses", intereses);

        NavBarVisualizer navBarVisualizer = new NavBarVisualizer();
        navBarVisualizer.colocarItems(user.getRoles(), model);

        context.render("crearComunidad.hbs", model);
    }


    public void eliminarComunidad(Context context) {
        int comunidadId = Integer.parseInt(context.pathParam("id"));
        repositorioComunidad.deleteById(comunidadId);
        context.redirect("/comunidades");
    }

    public void crearComunidad(Context context) {

        Comunidad nuevaComunidad = new Comunidad();
        List<Servicio> interesesSeleccionados = new ArrayList<>();
        List<Usuario> admins = new ArrayList<>();
        List<Miembro> miembros = new ArrayList<>();

        if(!Objects.equals(context.formParam("nombreComunidad"), "")) {
            nuevaComunidad.setNombre(context.formParam("nombreComunidad"));
        }
        for (Servicio interes : repositorioServicio.findAll()) {
            String checkboxParam = context.formParam(Integer.toString(interes.getId()));
            if (checkboxParam != null) {
                interesesSeleccionados.add(interes);
            }
        }
        Usuario user = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));
        admins.add(user);
        Miembro miembro = repositorioUsuario.findMiembroByUsuarioId(user.getId());
    if(miembro!=null){
        miembros.add(miembro);
        nuevaComunidad.setIntereses(interesesSeleccionados);
        nuevaComunidad.setAdmins(admins);
        nuevaComunidad.setMiembros(miembros);
        int idComunidad = repositorioComunidad.save(nuevaComunidad);
        Comunidad comunidad = repositorioComunidad.find(idComunidad);
        miembro.addComunidad(comunidad);
        repositorioUsuario.updateMiembro(miembro);

        context.redirect("/comunidad/"+idComunidad);}else {
        context.redirect("/comunidades/");
    }

    }

    public void mostrarComunidad(Context context) {
        Map<String, Object> model = new HashMap<>();
        int comunidadId = Integer.parseInt(context.pathParam("id"));
        Comunidad comunidad = repositorioComunidad.find(comunidadId);

        if (comunidad == null) {
            context.redirect("/");
            return;
        }

        int userId = Integer.parseInt(context.cookie("id"));
        Usuario user = repositorioUsuario.findUsuarioById(userId);
        model.put("username", context.cookie("username"));
        model.put("editarComunidad", user.tienePermiso("editarComunidad") || comunidad.hasAdmin(userId));
        model.put("esAdmin", comunidad.hasAdmin(userId));

        boolean esTipoMiembro = repositorioUsuario.findMiembroByUsuarioId(userId) != null;
        model.put("usuarioPerteneceAComunidad", comunidad.esMiembroByUserId(userId));
        model.put("usuarioEsTipoMiembro", esTipoMiembro);
        model.put("puedeUnirse", !comunidad.esMiembroByUserId(userId) && esTipoMiembro);

        model.put("comunidad", comunidad);

        List<Map<String, Object>> listaDeMiembros = new ArrayList<>();


        for (Miembro miembro : comunidad.getMiembros()) {
            Map<String, Object> miembroMap = new HashMap<>();
            miembroMap.put("nombre", miembro.getNombre());
            miembroMap.put("apellido", miembro.getApellido());
            miembroMap.put("id", miembro.getId());
            miembroMap.put("correoElectronico", miembro.getCorreoElectronico());
            miembroMap.put("telefono", miembro.getTelefono());
            boolean esAdmin = comunidad.hasAdmin(miembro.getUsuario().getId());
            miembroMap.put("MiembroNoEsAdmin", !esAdmin); // El opuesto de esAdmin
            listaDeMiembros.add(miembroMap);
        }


        model.put("listaMiembros",listaDeMiembros);

        model.put("listaDeServicios", repositorioServicio.findAll().stream()
                .filter(servicio -> !comunidad.deInteres(servicio))
                .collect(Collectors.toList()));

        NavBarVisualizer navBarVisualizer = new NavBarVisualizer();
        navBarVisualizer.colocarItems(user.getRoles(), model);

        context.render("comunidadDetalle.hbs", model);
    }


    public void expulsarMiembro(Context context) {
        int comunidadId = Integer.parseInt(context.pathParam("comunidadId"));
        int miembroId = Integer.parseInt(context.pathParam("miembroId"));

        Usuario user = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));
        Comunidad comunidad = repositorioComunidad.find(comunidadId);

        // Verifica si el usuario es administrador de la comunidad
        if (!comunidad.getAdmins().contains(user) && !user.usuarioTieneRol("admin")) {
            context.status(403).result("No autorizado");
            return;
        }

        Miembro miembro = comunidad.getMiembro(miembroId);
        comunidad.removeMiembro(miembro);
        miembro.removeComunidad(comunidad);
        repositorioUsuario.updateMiembro(miembro);
        repositorioComunidad.update(comunidad);

        context.redirect("/comunidad/" + comunidadId);
    }


    public void ascenderAAdmin(Context context) {
        int comunidadId = Integer.parseInt(context.pathParam("comunidadId"));
        int miembroId = Integer.parseInt(context.pathParam("miembroId"));

        Usuario user = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));
        Comunidad comunidad = repositorioComunidad.find(comunidadId);

        if (!comunidad.getAdmins().contains(user) && !user.usuarioTieneRol("admin")) {
            context.status(403).result("No autorizado");
            return;
        }
        Miembro miembro = comunidad.getMiembro(miembroId);
        comunidad.addAdmins(miembro.getUsuario());
        repositorioComunidad.update(comunidad);
        context.redirect("/comunidad/" + comunidadId);
    }
    public void removerAdmin(Context context) {
        int comunidadId = Integer.parseInt(context.formParam("comunidadId"));
        int adminId = Integer.parseInt(context.formParam("adminId"));

        Comunidad comunidad = repositorioComunidad.find(comunidadId);
        Usuario user = repositorioUsuario.findUsuarioById(adminId);
        comunidad.removerAdmin(adminId);

        if (comunidad.getAdmins().size() == 0 && comunidad.getMiembros().size() != 0) {
            for (Miembro miembro : comunidad.getMiembros()) {
                if (!miembro.getUsuario().equals(user)) {
                    comunidad.addAdmins(miembro.getUsuario());
                    break;
                }
            }
        }

        repositorioComunidad.update(comunidad);

        context.redirect("/comunidad/" + comunidadId);
    }

    public void joinComunidad(Context context)
    {
        Map<String, Object> model = new HashMap<>();
        int comunidadId = Integer.parseInt(context.pathParam("comunidadId"));
        int userId = Integer.parseInt(context.cookie("id"));

        Miembro miembro = repositorioUsuario.findMiembroByUsuarioId(userId);
        Comunidad comunidad = repositorioComunidad.find(comunidadId);

        if(comunidad.getAdmins().size()==0){
            comunidad.addAdmins(miembro.getUsuario());
        }

        miembro.addComunidad(comunidad);
        comunidad.agregarMiembros(miembro);

        repositorioComunidad.update(comunidad);
        repositorioUsuario.updateMiembro(miembro);
        context.redirect("/comunidad/" + comunidadId);
    }

    public void disJoinComunidad(Context context)
    {
        int comunidadId = Integer.parseInt(context.pathParam("comunidadId"));
        int userId = Integer.parseInt(context.cookie("id"));

        Miembro miembro = repositorioUsuario.findMiembroByUsuarioId(userId);
        Comunidad comunidad = repositorioComunidad.find(comunidadId);

        comunidad.removeMiembro(miembro);
        miembro.removeComunidad(comunidad);
        repositorioUsuario.updateMiembro(miembro);
        repositorioComunidad.update(comunidad);

        context.redirect("/comunidad/" + comunidadId);
    }

    public void addInteres(Context context)
    {
        Map<String, Object> model = new HashMap<>();

        Map<String, List<String>> parametros = context.formParamMap();

        for (Map.Entry<String, List<String>> entry : parametros.entrySet()) {
            System.out.println("Clave: " + entry.getKey() + ", Valor: " + entry.getValue());
        }
        int comunidadId = Integer.parseInt(context.formParam("comunidadId"));
        int servicioId = Integer.parseInt(context.formParam("servicioSeleccionado"));

        Comunidad comunidad = repositorioComunidad.find(comunidadId);
        Servicio servicio = repositorioServicio.findServicioById(servicioId);

        comunidad.agregarInteres(servicio);
        repositorioComunidad.update(comunidad);

        context.redirect("/comunidad/" + comunidadId);

    }
    public void removerInteres( Context context)
    {
        int comunidadId = Integer.parseInt(context.formParam("comunidadId"));
        int servicioId = Integer.parseInt(context.formParam("interesId"));

        Comunidad comunidad = repositorioComunidad.find(comunidadId);
        //Servicio servicio = repositorioServicio.findServicioById(servicioId);

        comunidad.removerInteres(servicioId);
        repositorioComunidad.update(comunidad);
        context.redirect("/comunidad/" + comunidadId);

    }


}




