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
import domain.entidades.TipoEntidad;
import domain.localizaciones.Direccion;
import domain.services.NavBarVisualizer;
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


        context.render("entidades.hbs", model);
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

        Handlebars handlebars = new Handlebars().with(new ClassPathTemplateLoader("/templates", ".hbs"));
        try {
            model.put("crearDireccion", handlebars.compile("common_CrearDireccion").apply(model));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        NavBarVisualizer navBarVisualizer = new NavBarVisualizer();
        navBarVisualizer.colocarItems(user.getRoles(), model);

        context.render("entidad_detalle.hbs", model);
    }

    public void indexTipoEntidad(Context context){
        Map<String, Object> model = new HashMap<>();
        model.put("username", context.cookie("username"));
        Usuario user = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));
        model.put("UserId",context.cookie("id"));
        NavBarVisualizer navBarVisualizer = new NavBarVisualizer();
        navBarVisualizer.colocarItems(user.getRoles(), model);

        List<TipoEntidad> listaTiposEntidades = repositorioEntidad.findAllTipoEntidad();
        model.put("tipoEntidad",listaTiposEntidades);

        context.render("tipoEntidad.hbs", model);

    }

    public void crearTipoEntidad(Context context){
        String nombreTipo = context.formParam("nombreTipo");

        TipoEntidad tipoEntidad = new TipoEntidad(nombreTipo);
        repositorioEntidad.save(tipoEntidad);
        context.redirect("/admin/tipoentidad");

    }

    public void crearEntidad(Context context)
    {
        String nombreEntidad = context.formParam("entidadNombre");
        String emailEntidad = context.formParam("entidadEmail");
        String descripcionEntidad = context.formParam("entidadDescripcion");
        int tipoEntidadId = Integer.parseInt(context.formParam("tipoEntidadId")); // Esto debería ser el ID del tipo de entidad seleccionado

        Entidad entidad = new Entidad(nombreEntidad, repositorioEntidad.findTipoEntidad(tipoEntidadId), emailEntidad, descripcionEntidad);
        repositorioEntidad.save(entidad);
        context.redirect("/admin/entidades");
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
