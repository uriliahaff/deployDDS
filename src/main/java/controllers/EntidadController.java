package controllers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import domain.Repositorios.RepositorioDireccion;
import domain.Repositorios.RepositorioEntidad;
import domain.Repositorios.RepositorioEstablecimiento;
import domain.Repositorios.RepositorioUsuario;
import domain.Usuarios.Comunidades.Comunidad;
import domain.Usuarios.Comunidades.Miembro;
import domain.Usuarios.EntidadPrestadora;
import domain.Usuarios.Usuario;
import domain.entidades.Entidad;
import domain.entidades.Establecimiento;
import domain.localizaciones.Direccion;
import domain.services.georef.entities.Localidad;
import domain.services.georef.entities.Municipio;
import domain.services.georef.entities.Provincia;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntidadController
{
    private RepositorioEntidad repositorioEntidad = new RepositorioEntidad();
    private RepositorioUsuario repositorioUsuario = new RepositorioUsuario();
    private RepositorioDireccion repositorioDireccion = new RepositorioDireccion();
    private RepositorioEstablecimiento repositorioEstablecimiento = new RepositorioEstablecimiento();
    public void indexEntidades(Context context) {
        Map<String, Object> model = new HashMap<>();
        model.put("username", context.cookie("username"));
        Usuario user = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));

        CommonController.fillNav(model, user);

        List<Entidad> entidades = repositorioEntidad.findAll();
        model.put("entidades", entidades);

        model.put("editarEntidades", user.tienePermiso("editarEntidades"));


        model.put("tipos",repositorioEntidad.findAllTipoEntidad());
        try {
            // Configura el loader para buscar plantillas en el directorio /templates
            Handlebars handlebars = new Handlebars().with(new ClassPathTemplateLoader("/templates", ".hbs"));

            // Compila el contenido del partial 'incidentes_template' y pásalo como 'body'
            Template template = handlebars.compile("entidades");
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
    public void indexEntidad(Context context)
    {
        Map<String, Object> model = new HashMap<>();
        model.put("username", context.cookie("username"));
        Usuario user = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));
        model.put("UserId",context.cookie("id"));

        CommonController.fillNav(model, user);
        int entidadId = Integer.parseInt(context.pathParam("id"));
        Entidad entidad = repositorioEntidad.findEntidadById(entidadId);
        entidad.getEstablecimientos().size();
        model.put("entidad", entidad);

        EntidadPrestadora entidadPrestadora = repositorioUsuario.findEntidadPrestadoraByUserId(user.getId());
        model.put("editarEntidades", user.tienePermiso("editarEntidades") || (entidadPrestadora != null && entidadPrestadora.getEntidad().getId() == entidadId));

        model.put("tipos",repositorioEntidad.findAllTipoEntidad());
        model.put("provincias",repositorioDireccion.findAllProvincias());
        model.put("localidades",repositorioDireccion.findAllLocalidades());
        model.put("municipios",repositorioDireccion.findAllMunicipios());
        /*try {
            // Configura el loader para buscar plantillas en el directorio /templates
            Handlebars handlebars = new Handlebars().with(new ClassPathTemplateLoader("/templates", ".hbs"));


            Template templateCrearDireccion = handlebars.compile("common_CrearDireccion");
            String formBodyContent = templateCrearDireccion.apply(model);
            model.put("crearDireccion", formBodyContent);
            // Compila el contenido del partial 'incidentes_template' y pásalo como 'body'
            Template template = handlebars.compile("entidad_detalle");
            String bodyContent = template.apply(model);
            model.put("body", bodyContent);


        } catch (IOException e) {
            e.printStackTrace();
            // Es importante manejar esta excepción adecuadamente
            context.status(500).result("Error al procesar la plantilla de incidentes.");
            return; // Sal del método aquí si no quieres procesar más el request debido al error
        }*/
        Handlebars handlebars = new Handlebars().with(new ClassPathTemplateLoader("/templates", ".hbs"));
        try {
            model.put("crearDireccion", handlebars.compile("common_CrearDireccion").apply(model));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Renderiza la plantilla común con el contenido incluido
       // context.render("layout_comun.hbs", model);
        context.render("entidad_detalle.hbs", model);
    }

    public void crearEntidad(Context context)
    {
        String nombreEntidad = context.formParam("entidadNombre");
        String emailEntidad = context.formParam("entidadEmail");
        String descripcionEntidad = context.formParam("entidadDescripcion");
        int tipoEntidadId = Integer.parseInt(context.formParam("tipoEntidadId")); // Esto debería ser el ID del tipo de entidad seleccionado

        Entidad entidad = new Entidad(nombreEntidad, repositorioEntidad.findTipoEntidad(tipoEntidadId), emailEntidad, descripcionEntidad);
        repositorioEntidad.save(entidad);
        context.redirect("/entidades");
    }
    public void crearEstablecimiento(Context context)
    {
        int entidadId = Integer.parseInt(context.pathParam("id"));
        Entidad entidad = repositorioEntidad.findEntidadById(entidadId);
        String establecimientoNombre = context.formParam("eNombre");
        String establecimientoDescripcion = context.formParam("eDescripcion");

        System.out.println(establecimientoNombre);

        String provinciaIdRaw = context.formParam("provinciaId");
        String localidadIdRaw = context.formParam("localidadId");
        String municipioIdRaw = context.formParam("municipioId");

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
        Establecimiento establecimiento = new Establecimiento(establecimientoNombre,establecimientoDescripcion,direccion);
        establecimiento.setEntidad(entidad);
        repositorioEstablecimiento.save(establecimiento);
        entidad.agregarEstablecimiento(establecimiento);

        repositorioEntidad.update(entidad);
        context.redirect("/entidad/"+ entidadId);
    }

}
