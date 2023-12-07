package controllers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import domain.Repositorios.RepositorioComunidad;
import domain.Repositorios.RepositorioServicio;
import domain.Repositorios.RepositorioUsuario;
import domain.Usuarios.OrganismoDeControl;
import domain.Usuarios.Usuario;
import domain.services.NavBarVisualizer;
import domain.services.csvdataloader.CSVDataLoader;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganismoDeControlController
{
    private static RepositorioUsuario repositorioUsuario = new RepositorioUsuario();
    private static RepositorioServicio repositorioServicio = new RepositorioServicio();

    public void indexOrganismoDeControl(Context context)
    {
        Map<String, Object> model = new HashMap<>();
        model.put("username", context.cookie("username"));
        Usuario user = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));
        boolean isAdmin = user.usuarioTieneRol("admin");
        boolean isODC = repositorioUsuario.findOrganismoDeControlByUserId(user.getId()) != null;

        if(!isAdmin && !isODC) {
            context.redirect("/");
        }
        model.put("isAdmin",isAdmin);

        NavBarVisualizer navBarVisualizer = new NavBarVisualizer();
        navBarVisualizer.colocarItems(user.getRoles(), model);
        context.render("organismoDeControl.hbs", model);
    }

    public void cargarMasivaDeOrganismosDeControl(Context context) {
        // CARGA MASIVA
        UploadedFile archivo = context.uploadedFile("archivo");

        if (archivo != null) {
            try (InputStream inputStream = archivo.content()) {
                CSVDataLoader csvDataLoader = new CSVDataLoader();
                List<OrganismoDeControl> organismosDeControl = csvDataLoader.leerArchivoOrganismo(inputStream);

                repositorioUsuario.saveOrganismosDeControl(organismosDeControl);


                context.redirect("/organismoDeControl");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
