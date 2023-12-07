package controllers;

import domain.Repositorios.RepositorioDireccion;
import domain.Repositorios.RepositorioEntidadPrestadoraOrganismoControl;
import domain.Repositorios.RepositorioUsuario;
import domain.Usuarios.Comunidades.Comunidad;
import domain.Usuarios.Usuario;
import domain.services.NavBarVisualizer;
import domain.services.georef.ServicioGeoref;
import domain.services.georef.entities.*;
import io.javalin.http.Context;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GEOREFController {

    private RepositorioDireccion repoGEOREF;
    private RepositorioUsuario repositorioUsuario;

    public GEOREFController(RepositorioDireccion repoGEOREF, RepositorioUsuario repoUser){
        this.repoGEOREF = repoGEOREF;
        this.repositorioUsuario = repoUser;
    }
    public void index(Context context){
        Map<String, Object> model = new HashMap<>();
        model.put("username", context.cookie("username"));
        Usuario user = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));
        model.put("UserId",context.cookie("id"));

        NavBarVisualizer navBarVisualizer = new NavBarVisualizer();
        navBarVisualizer.colocarItems(user.getRoles(), model);

        context.render("georef.hbs", model);
    }

    public void actualizar(Context context) throws IOException {
        ServicioGeoref servicio = ServicioGeoref.getInstancia();
        ListadoDeProvincias provinciasNuevas = servicio.listadoDeProvincias();

        for(Provincia provinciaNueva:provinciasNuevas.provincias){
            Provincia provincia = repoGEOREF.findProvincia(provinciaNueva.getId());
            if (provincia != null) {
                System.out.println(provincia.getNombre());
                // Actualizar la provincia existente
                repoGEOREF.updateProvincia(new Provincia(provinciaNueva.getNombre(), provinciaNueva.getId()));
            } else {
                // Guardar nueva provincia
                repoGEOREF.saveProvincia(new Provincia(provinciaNueva.getNombre(), provinciaNueva.getId()));
            }
            System.out.println("PROVINCIA: "+provinciaNueva.getNombre());
            ListadoDeMunicipios municipiosNuevos = servicio.listadoDeMunicipiosDeProvincia(provinciaNueva.getId());

            for(Municipio municipioNuevo: municipiosNuevos.municipios){
                if(repoGEOREF.findMunicipio(municipioNuevo.getId()) != null)
                    repoGEOREF.updateMunicipio(new Municipio(municipioNuevo.getNombre(), municipioNuevo.getId()));
                else
                    repoGEOREF.saveMunicipio(new Municipio(municipioNuevo.getNombre(), municipioNuevo.getId()));
                System.out.println("MUNICIPIO: "+municipioNuevo.getNombre());

                ListadoDeLocalidades localidadesNuevas = servicio.listadoDeLocalidadesDeMunicipio(municipioNuevo.getId());
                for(Localidad localidadNueva: localidadesNuevas.localidades){
                    if(repoGEOREF.findLocalidad(localidadNueva.getId()) != null)
                        repoGEOREF.updateLocalidad(new Localidad(localidadNueva.getNombre(), localidadNueva.getId()));
                    else
                        repoGEOREF.saveLocalidad(new Localidad(localidadNueva.getNombre(), localidadNueva.getId()));
                    System.out.println("LOCALIDAD: "+localidadNueva.getNombre());
                }
            }

        }

        context.redirect("/admin/georef");
    }

    public void limpiarDatos(){

    }



}
