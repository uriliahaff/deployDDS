package controllers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import domain.Repositorios.*;
import domain.Usuarios.EntidadPrestadora;
import domain.Usuarios.Usuario;
import domain.entidades.Entidad;
import domain.entidades.Establecimiento;
import domain.services.NavBarVisualizer;
import domain.servicios.Estado;
import domain.servicios.PrestacionDeServicio;
import domain.servicios.Servicio;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstablecimientoController
{
    private RepositorioEntidad repositorioEntidad = new RepositorioEntidad();
    private RepositorioUsuario repositorioUsuario = new RepositorioUsuario();

    private RepositorioServicio repositorioServicio = new RepositorioServicio();
    private RepositorioEstablecimiento repositorioEstablecimiento = new RepositorioEstablecimiento();

    private RepositorioPrestacionDeServicio repositorioPrestacionDeServicio = new RepositorioPrestacionDeServicio();

    public void indexEstablecimiento(Context context) {
        Map<String, Object> model = new HashMap<>();
        model.put("username", context.cookie("username"));
        Usuario user = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));
        model.put("UserId",context.cookie("id"));
        int establecimientoId = Integer.parseInt(context.pathParam("id"));
        Establecimiento establecimiento = repositorioEstablecimiento.find(establecimientoId);

        List<EntidadPrestadora> entidadesPrestadora = repositorioUsuario.findEntidadesPrestadoraslByUserId(user.getId());
        boolean contieneEntidad = false;
        if (entidadesPrestadora != null) {
            for (EntidadPrestadora entidadPrestadora : entidadesPrestadora) {
                if (entidadPrestadora.getEntidad() == establecimiento.getEntidad()) {
                    contieneEntidad = true;
                    break;
                }
            }
        }

        model.put("editarEntidades", (user.tienePermiso("editarEntidades") ||  contieneEntidad));

        //CommonController.fillNav(model, user);

        List<Entidad> entidades = repositorioEntidad.findAll();
        model.put("entidades", entidades);

        model.put("establecimiento", establecimiento);


        model.put("servicios", repositorioServicio.findAll());

        NavBarVisualizer navBarVisualizer = new NavBarVisualizer();
        navBarVisualizer.colocarItems(user.getRoles(), model);

        context.render("establecimientoDetalle.hbs", model);
    }

    public void addPrestacion(Context context)
    {
        int establecimientoId = Integer.parseInt(context.pathParam("id"));
        int servicioId = Integer.parseInt(context.formParam("servicioId"));
        Establecimiento establecimiento = repositorioEstablecimiento.find(establecimientoId);
        Servicio servicio = repositorioServicio.findServicioById(servicioId);
        String estadoForm = context.formParam("estado");
        Estado estado;
        if (estadoForm.equals("ACTIVO"))
            estado = Estado.IN_SERVICE;
        else
            estado = Estado.OUT_OF_SERVICE;
        /*
        * IN_SERVICE
        * OUT_OF_SERVICE
        * */
        PrestacionDeServicio prestacionDeServicio = new PrestacionDeServicio(servicio,establecimiento, estado);

        repositorioPrestacionDeServicio.save(prestacionDeServicio);
        establecimiento.agregarServicio(prestacionDeServicio);
        repositorioEstablecimiento.update(establecimiento);

        context.redirect("/establecimiento/" +establecimientoId);

    }

}
