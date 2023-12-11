package controllers;

import domain.Repositorios.*;

public class FactoryController {

    public static Object controller(String nombre){
        Object controller = null;



        switch (nombre){
            case "login": controller = new LoginController(new RepositorioUsuario()); break;
            case "entidades": controller = new EntidadesOrganismosController(new RepositorioEntidadPrestadoraOrganismoControl(), new RepositorioUsuario()); break;
            case "organismos": controller = new EntidadesOrganismosController(new RepositorioEntidadPrestadoraOrganismoControl(), new RepositorioUsuario()); break;
            case "usuarios": controller = new UsuariosController(new RepositorioUsuario()); break;
            case "rankings": controller = new RankingController(new RepositorioUsuario()); break;
            case "incidentes": controller = new IncidenteController(new RepositorioIncidente(), new RepositorioUsuario(), new RepositorioEntidad()); break;
            case "comunidad": controller = new ComunidadController(); break;
            case "servicios": controller = new ServicioController(); break;
            case "organismoDeControl": controller = new OrganismoDeControlController(); break;
            case "signIn": controller = new SignInController(); break;
            case "perfil": controller = new PerfilController(); break;
            case "entidad": controller = new EntidadController(); break;
            case "establecimiento": controller = new EstablecimientoController(); break;
            case "rol": controller = new RolController(); break;
            case "georef": controller = new GEOREFController(new RepositorioDireccion(), new RepositorioUsuario()); break;

        }
        return controller;
    }


}
