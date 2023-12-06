package controllers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import domain.Repositorios.*;
import domain.Usuarios.EntidadPrestadora;
import domain.Usuarios.Usuario;
import domain.entidades.Entidad;
import domain.entidades.Establecimiento;
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

        EntidadPrestadora entidadPrestadora = repositorioUsuario.findEntidadPrestadoraByUserId(user.getId());
        model.put("editarEntidades", user.tienePermiso("editarEntidades") || (entidadPrestadora != null && entidadPrestadora.getEntidad().getId() == establecimiento.getEntidad().getId()));


        //CommonController.fillNav(model, user);

        List<Entidad> entidades = repositorioEntidad.findAll();
        model.put("entidades", entidades);

        model.put("establecimiento", establecimiento);


        model.put("servicios", repositorioServicio.findAll());

        try {
            // Configura el loader para buscar plantillas en el directorio /templates
            Handlebars handlebars = new Handlebars().with(new ClassPathTemplateLoader("/templates", ".hbs"));

            // Compila el contenido del partial 'incidentes_template' y pásalo como 'body'
            Template template = handlebars.compile("establecimiento_detalle");
            String bodyContent = template.apply(model);
            model.put("body", bodyContent);
        } catch (IOException e) {
            e.printStackTrace();
            // Es importante manejar esta excepción adecuadamente
            context.status(500).result("Error al procesar la plantilla de incidentes.");
            return; // Sal del método aquí si no quieres procesar más el request debido al error
        }

        // Renderiza la plantilla común con el contenido incluido
        context.render("layout_comun.hbs", model);
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
