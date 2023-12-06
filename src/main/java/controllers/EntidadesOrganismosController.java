package controllers;

import domain.Repositorios.RepositorioEntidadPrestadoraOrganismoControl;
import domain.Repositorios.RepositorioUsuario;
import domain.Usuarios.EntidadPrestadora;
import domain.Usuarios.OrganismoDeControl;
import domain.Usuarios.Usuario;
import domain.entidades.Establecimiento;
import domain.services.NavBarVisualizer;
import domain.services.csvdataloader.CSVDataLoader;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntidadesOrganismosController {
    private EntityManager entityManager;

    //private RepositorioEntidadPrestadoraOrganismoControl repositorioDeEntidadesPrestadoras;

    private RepositorioUsuario repositorioUsuario = new RepositorioUsuario();


    public EntidadesOrganismosController(RepositorioEntidadPrestadoraOrganismoControl repo, RepositorioUsuario repoUsuario){
        //this.repositorioDeEntidadesPrestadoras = repo;
        //this.repositorioUsuario = repoUsuario;
    }
    public void indexEntidades(Context context){
        Map<String, Object> model = new HashMap<>();
        List<EntidadPrestadora> entidades = repositorioUsuario.findAllEntidadPrestadora();
        model.put("entidades", entidades);
        model.put("username", context.cookie("username"));
        model.put("UserId",context.cookie("id"));

        Usuario user = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));
        NavBarVisualizer navBarVisualizer = new NavBarVisualizer();
        navBarVisualizer.colocarItems(user.getRoles(), model);

        context.render("cargaEntidades.hbs", model);
    }

    public void indexOrganismos(Context context){
        Map<String, Object> model = new HashMap<>();
        List<OrganismoDeControl> organismos = repositorioUsuario.findAllOrganismoDeControl();
        model.put("organismos", organismos);
        model.put("username", context.cookie("username"));
        model.put("UserId",context.cookie("id"));
        
        Usuario user = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));
        NavBarVisualizer navBarVisualizer = new NavBarVisualizer();
        navBarVisualizer.colocarItems(user.getRoles(), model);

        context.render("cargaOrganismos.hbs", model);
    }

    public void cargarMasivaEntidades(Context context) {
        // CARGA MASIVA
        UploadedFile archivo = context.uploadedFile("archivo");

        if (archivo != null) {
            try (InputStream inputStream = archivo.content()) {
                CSVDataLoader csvDataLoader = new CSVDataLoader();
                List<EntidadPrestadora> entidadesACargar = csvDataLoader.leerArchivoEntidades(inputStream);

                repositorioUsuario.saveEntidadesPrestadoras(entidadesACargar);

                context.redirect("/entidades");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public void cargarMasivaOrganismos(Context context) {
        // CARGA MASIVA
        UploadedFile archivo = context.uploadedFile("archivo");

        if (archivo != null) {
            try (InputStream inputStream = archivo.content()) {
                CSVDataLoader csvDataLoader = new CSVDataLoader();
                List<OrganismoDeControl> organismosDeControl = csvDataLoader.leerArchivoOrganismo(inputStream);

                repositorioUsuario.saveOrganismosDeControl(organismosDeControl);


                context.redirect("/cargaOrganismos");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public void save(EntidadPrestadora entidad) {
        entityManager.getTransaction().begin();
        entityManager.persist(entidad);
        entityManager.getTransaction().commit();
    }



}
