package sever;

import controllers.*;
import domain.Repositorios.RepositorioUsuario;
import domain.Usuarios.Rol;
import io.javalin.http.Context;

public class Router {

    public static void init() {

      /*  Server.app().before(ctx -> {
            // Excluir la verificación de autenticación para la ruta /login
            if (!ctx.path().equals("/login") && !isLoggedIn(ctx)) {
                // Redirigir o manejar la falta de autenticación según tus necesidades
                ctx.redirect("/login");
            }
            if (ctx.path().equals("/login") && isLoggedIn(ctx)) {
                // Redirigir o manejar la falta de autenticación según tus necesidades
                ctx.redirect("/");
            }
        });
*/
        Server.app().routes(()-> {

            Rol admin = new Rol("admin", null);
            Rol adminEntidad = new Rol("adminEntidad", null);
            Rol adminOrganismo= new Rol("adminOrganismo", null);

            Server.app().get("/", ((LoginController) FactoryController.controller("login"))::admin);
            Server.app().get("/login", ((LoginController) FactoryController.controller("login"))::index);
            Server.app().get("/signin", ((LoginController) FactoryController.controller("login"))::registro);

            Server.app().get("/registrar", ((SignInController) FactoryController.controller("signIn"))::signInMember);


            Server.app().get("/logout", ((LoginController) FactoryController.controller("login"))::logout);
            Server.app().post("/login", ((LoginController) FactoryController.controller("login"))::loginAttempt);
            Server.app().post("/signin", ((LoginController) FactoryController.controller("login"))::signinAttempt);

            Server.app().get("/cargaEntidades",
                    ((EntidadesOrganismosController) FactoryController.controller("entidades"))::indexEntidades,
                    admin);
            Server.app().get("/cargaOrganismos",
                    ((EntidadesOrganismosController) FactoryController.controller("organismos"))::indexOrganismos,
                    admin);
            Server.app().post("/cargarCSVEntidades",
                    ((EntidadesOrganismosController) FactoryController.controller("entidades"))::cargarMasivaEntidades,
                    admin);
            Server.app().post("/cargarCSVOrganismos",
                    ((EntidadesOrganismosController) FactoryController.controller("organismos"))::cargarMasivaOrganismos,
                    admin);
            Server.app().get("/usuarios",
                    ((UsuariosController) FactoryController.controller("usuarios"))::index,
                    admin);
            Server.app().get("/usuarios/{id}/editar",
                    ((UsuariosController) FactoryController.controller("usuarios"))::editar,
                    admin);
            Server.app().post("/editarUsuario/{id}",
                    ((UsuariosController) FactoryController.controller("usuarios"))::update,
                    admin);
            Server.app().post("/eliminarUsuario/{id}",
                    ((UsuariosController) FactoryController.controller("usuarios"))::delete,
                    admin);
            Server.app().get("/rankings",
                    ((RankingController) FactoryController.controller("rankings"))::index,
                    adminEntidad, adminOrganismo);
            Server.app().get("/ranking/{id}",
                    ((RankingController) FactoryController.controller("rankings"))::ranking,
                    adminEntidad, adminOrganismo);
            Server.app().get("/admin/incidentes",
                    ((IncidenteController) FactoryController.controller("incidentes"))::index,
                    admin);
            Server.app().get("/aperturaIncidentes", ((IncidenteController) FactoryController.controller("incidentes"))::aperturaIncidentes);
            Server.app().post("/aperturaIncidente", ((IncidenteController) FactoryController.controller("incidentes"))::abrirIncidente);
            Server.app().get("/incidentes", ((IncidenteController) FactoryController.controller("incidentes"))::indexUser);
            Server.app().get("/cerrarIncidente/{id}", ((IncidenteController) FactoryController.controller("incidentes"))::cerrarIncidente);
            Server.app().get("/revisionIncidente/{id}", ((IncidenteController) FactoryController.controller("incidentes"))::revisionIncidente);


            Server.app().get("/comunidades", ((ComunidadController) FactoryController.controller("comunidad"))::indexComunidades);
            Server.app().post("/comunidad/delete/{id}", ((ComunidadController) FactoryController.controller("comunidad"))::eliminarComunidad);

            Server.app().get("/comunidad/{id}", ((ComunidadController) FactoryController.controller("comunidad"))::mostrarComunidad);

            Server.app().post("/comunidad/{comunidadId}/expulsar/{miembroId}", ((ComunidadController) FactoryController.controller("comunidad"))::expulsarMiembro);


            Server.app().post("/comunidad/{comunidadId}/ascender/{miembroId}", ((ComunidadController) FactoryController.controller("comunidad"))::ascenderAAdmin);
            Server.app().post("/comunidad/removerAdmin", ((ComunidadController) FactoryController.controller("comunidad"))::removerAdmin);

            Server.app().post("/comunidad/join/{comunidadId}", ((ComunidadController) FactoryController.controller("comunidad"))::joinComunidad);

            Server.app().post("/comunidad/addInteres", ((ComunidadController) FactoryController.controller("comunidad"))::addInteres);
            Server.app().post("/comunidad/removerInteres", ((ComunidadController) FactoryController.controller("comunidad"))::removerInteres);



            Server.app().get("/servicios", ((ServicioController) FactoryController.controller("servicios"))::indexServicios);
            Server.app().post("/crearServicio", ((ServicioController) FactoryController.controller("servicios"))::crearServicio);

            Server.app().get("/organismoDeControl", ((OrganismoDeControlController) FactoryController.controller("organismoDeControl"))::indexOrganismoDeControl);
            Server.app().post("/cargarMasivaDeOrganismosDeControl", ((OrganismoDeControlController) FactoryController.controller("organismoDeControl"))::indexOrganismoDeControl);




        });

        //  Server.app().get("/entidadesPrestadoras", new EntidadesPrestadorasController()::index);



    }

    private static boolean isLoggedIn(Context ctx) {
        // Verificar la presencia de la cookie de autenticación u otros indicadores de inicio de sesión
        return ctx.cookie("id") != null;
    }

}
