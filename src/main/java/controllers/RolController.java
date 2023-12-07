package controllers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import domain.Repositorios.RepositorioRol;
import domain.Repositorios.RepositorioUsuario;
import domain.Usuarios.Permiso;
import domain.Usuarios.Rol;
import domain.Usuarios.Usuario;
import domain.services.NavBarVisualizer;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RolController
{
    private RepositorioUsuario repositorioUsuario = new RepositorioUsuario();
    private RepositorioRol repositorioRol = new RepositorioRol();
    public void indexRoles(Context context)
    {
        Map<String,Object> model = new HashMap<>();
        model.put("UserId",context.cookie("id"));
        model.put("username", context.cookie("username"));

        Usuario user = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));
        List<Rol> roles = repositorioRol.findAllRoles();
        roles.size();
        roles.forEach(rol -> rol.getPermisos().size());
        List<Permiso> permisos = repositorioRol.findAllPermisos();

        roles.forEach(rol -> rol.getPermisos().forEach(permiso -> permiso.getDescripcion()));
        model.put("roles",roles);
        model.put("permisosDisponibles",permisos);
        model.put("UserId", context.cookie("id"));

        NavBarVisualizer navBarVisualizer = new NavBarVisualizer();
        navBarVisualizer.colocarItems(user.getRoles(), model);

        context.render("roles.hbs", model);
    }

    public void indexPermisos(Context context){
        Map<String,Object> model = new HashMap<>();
        model.put("UserId",context.cookie("id"));
        model.put("username", context.cookie("username"));
        Usuario user = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));
        List<Permiso> permisos = repositorioRol.findAllPermisos();
        model.put("permisos", permisos);

        NavBarVisualizer navBarVisualizer = new NavBarVisualizer();
        navBarVisualizer.colocarItems(user.getRoles(), model);

        context.render("permisos.hbs", model);
    }

    public void crearPermiso(Context context){
        String permisoName = context.formParam("descripcion");
        if(permisoName!=null && !permisoName.isEmpty())
            repositorioRol.save(new Permiso(permisoName));
        context.redirect("/admin/admin/permisos");
    }

    public void addPermiso(Context context)
    {
        int rolId = Integer.parseInt(context.pathParam("id"));
        int permisoId = Integer.parseInt(context.formParam("permisoId"));

        System.out.println(rolId + "\n" + permisoId);
        Rol rol = repositorioRol.findRolById(rolId);
        Permiso permiso = repositorioRol.findPermisoById(permisoId);
        rol.getPermisos().add(permiso);
        repositorioRol.update(rol);
        context.redirect("/admin/roles");

    }
    public void borrarPermiso(Context context)
    {
        int rolId = Integer.parseInt(context.pathParam("id"));
        int permisoId = Integer.parseInt(context.pathParam("permisoId"));

        Rol rol = repositorioRol.findRolById(rolId);
        Permiso permiso = repositorioRol.findPermisoById(permisoId);
        rol.getPermisos().remove(permiso);
        repositorioRol.update(rol);
        context.redirect("/admin/roles");

    }

    public void crearRol(Context context)
    {
        String rolName = context.formParam("nuevoRolNombre");
        if(rolName!=null && !rolName.isEmpty())
            repositorioRol.save(new Rol(rolName, new ArrayList<>()));
        context.redirect("/admin/roles");
    }
}
