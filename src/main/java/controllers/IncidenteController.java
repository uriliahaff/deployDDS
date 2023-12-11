package controllers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import domain.Repositorios.RepositorioIncidente;
import domain.Repositorios.RepositorioServicio;
import domain.Repositorios.RepositorioUsuario;
import domain.Usuarios.Comunidades.Comunidad;
import domain.Usuarios.Comunidades.Miembro;
import domain.Usuarios.Usuario;
import domain.informes.Incidente;
import domain.services.NavBarVisualizer;
import domain.services.notificadorDeIncidentes.NotificadorDeIncidentes;
import domain.servicios.Estado;
import domain.servicios.PrestacionDeServicio;
import io.javalin.http.Context;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.time.format.DateTimeFormatter;

import static java.util.stream.Collectors.toList;

public class IncidenteController {


    private RepositorioIncidente repositorioDeIncidentes;
    private RepositorioUsuario repositorioUsuario = new RepositorioUsuario();

    public IncidenteController(RepositorioIncidente repo, RepositorioUsuario repositorioUsuario){
        this.repositorioDeIncidentes = repo;
        this.repositorioUsuario = repositorioUsuario;
    }

public void index(Context context){
    Map<String, Object> model = new HashMap<>();
    List<Incidente> incidentes = this.repositorioDeIncidentes.findAll();
    model.put("username", context.cookie("username"));
    Usuario user = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));
    NavBarVisualizer navBarVisualizer = new NavBarVisualizer();
    navBarVisualizer.colocarItems(user.getRoles(), model);

    // Formatear la fecha de inicio
    List<String> fechasInicioFormateadas = incidentes.stream()
            .map(incidente -> incidente.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
            .toList();
// Formatear la fecha de fin
    List<String> fechasFinFormateadas = incidentes.stream()
            .map(incidente -> incidente.getFechaCierre() != null ? incidente.getFechaCierre().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null)
            .toList();
    for (int i = 0; i < fechasInicioFormateadas.size(); i++) {
        String fechaInicioFormateada = fechasInicioFormateadas.get(i);
        String fechaFinFormateada = fechasFinFormateadas.get(i);
        Incidente incidente = incidentes.get(i);
        incidente.setFechaInicioFormateada(fechaInicioFormateada);
        incidente.setFechaFinFormateada(fechaFinFormateada);

    }
    model.put("incidentes", incidentes);


    context.render("incidentes.hbs", model);
}


public void indexIncidentes(Context context) {
    Map<String, Object> model = new HashMap<>();
    List<Incidente> incidentes = this.repositorioDeIncidentes.findAllOpen();
    int userId = Integer.parseInt(context.cookie("id"));
    model.put("UserId",context.cookie("id"));

    Miembro miembro = repositorioUsuario.findMiembroByUsuarioId(userId);
    System.out.println(miembro);
    if (miembro!= null)
    {
        incidentes = incidentes
                .stream()
                .filter(incidente -> incidente.esMiembroEnComunidadesAfectadas(miembro))
                .toList();
        System.out.println(incidentes.size());
    }

    incidentes.forEach(incidente -> incidente.setFechaInicioFormateada(incidente.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
    incidentes.stream().filter(incidente -> !incidente.estaAbierto()).forEach(incidente -> incidente.setFechaFinFormateada(incidente.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
    model.put("incidentes",incidentes);
    model.put("username", context.cookie("username"));
    Usuario user = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));



    NavBarVisualizer navBarVisualizer = new NavBarVisualizer();
    navBarVisualizer.colocarItems(user.getRoles(), model);
    context.render("incidentesUser.hbs", model);
}


    public void aperturaIncidentes(Context context){
        Map<String, Object> model = new HashMap<>();

        RepositorioServicio repositorioServicios = new RepositorioServicio();
        List<PrestacionDeServicio> prestacionDeServicios = repositorioServicios.buscarTodasLasPrestaciones();

        model.put("username", context.cookie("username"));
        model.put("UserId", context.cookie("id"));
        model.put("prestaciones", prestacionDeServicios);
        Usuario user = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));
        NavBarVisualizer navBarVisualizer = new NavBarVisualizer();
        navBarVisualizer.colocarItems(user.getRoles(), model);

        context.render("aperturaIncidente.hbs", model);
    }

    public void cerrarIncidente(Context context){

        Incidente incidente = repositorioDeIncidentes.findById(Integer.parseInt(context.pathParam("id")));

        LocalDateTime fechaActual = LocalDateTime.now();
        RepositorioUsuario repositorioUsuario = new RepositorioUsuario();


        Usuario usuarioInformante = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));
        Miembro miembro = repositorioUsuario.findMiembroByUsuarioId(usuarioInformante.getId());

        List <Incidente> incidentesAbiertos = repositorioDeIncidentes.findAllOpen();

        boolean hayOtrosIncidentes = incidentesAbiertos.stream()
                .anyMatch(i -> i.getServicioAfectado().equals(incidente.getServicioAfectado()) &&
                        i != incidente &&
                        !Collections.disjoint(i.getComunidadesAfectadas(), miembro.getComunidades()));

        if(!hayOtrosIncidentes){
            incidente.getServicioAfectado().setEstado(Estado.IN_SERVICE);
        }

        incidente.cerrarIncidente(fechaActual, miembro);
        this.repositorioDeIncidentes.update(incidente);
        NotificadorDeIncidentes.notificarIncidente(incidente);

        context.redirect("/incidentes");
    }

    public void revisionIncidente(Context context){
        Map<String, Object> model = new HashMap<>();
        model.put("username", context.cookie("username"));
        Incidente incidente = this.repositorioDeIncidentes.findById(Integer.parseInt(context.pathParam("id")));
        model.put("incidente", incidente);
        Usuario user = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));
        NavBarVisualizer navBarVisualizer = new NavBarVisualizer();
        navBarVisualizer.colocarItems(user.getRoles(), model);
        context.render("revisionIncidente.hbs", model);
    }

    public void abrirIncidente(Context context){
        Incidente incidente = new Incidente();
        int userId = Integer.parseInt(context.cookie("id"));

        Miembro miembro = repositorioUsuario.findMiembroByUsuarioId(userId);
        incidente.setMiembroInformante(miembro);
        this.asignarParametros(incidente, context);

        LocalDateTime fechaActual = LocalDateTime.now();

        incidente.setFechaInicio(fechaActual);

        if(miembro.getComunidades().size()!=0){
            incidente.getServicioAfectado().setEstado(Estado.OUT_OF_SERVICE);
            List<Comunidad> comunidadesAfectadas = miembro.getComunidades();
            incidente.addComunidadesAfectadas(comunidadesAfectadas);
            this.repositorioDeIncidentes.save(incidente);
            NotificadorDeIncidentes.notificarIncidente(incidente);
        }


        context.redirect("/incidentes");

    }

    private void asignarParametros(Incidente incidente, Context context) {

        if(!Objects.equals(context.formParam("descripcion"), "")) {
            incidente.setDescripcion(context.formParam("descripcion"));
        }
        if(!Objects.equals(context.formParam("prestacion"), "")) {
            RepositorioServicio repositorioServicio = new RepositorioServicio();
            PrestacionDeServicio prestacion = repositorioServicio.findPrestacionById(Integer.parseInt(context.formParam("prestacion")));
            incidente.setServicioAfectado(prestacion);
        }


    }



}
