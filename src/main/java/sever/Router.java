package sever;

import controllers.*;
import domain.Usuarios.Rol;
import io.javalin.http.Context;

public class Router {

    public static void init() {

        Server.app().routes(()-> {

            Rol admin = new Rol("admin", null);
            Rol adminEntidad = new Rol("adminEntidad", null);
            Rol adminOrganismo= new Rol("adminOrganismo", null);


            Server.app().get("/", ((LoginController) FactoryController.controller("login"))::admin);
            Server.app().get("/login", ((LoginController) FactoryController.controller("login"))::index);
            Server.app().post("/login", ((LoginController) FactoryController.controller("login"))::loginAttempt);
            Server.app().get("/logout", ((LoginController) FactoryController.controller("login"))::logout);
            Server.app().get("/signin", ((SignInController) FactoryController.controller("signIn"))::renderSignInMember);
            Server.app().get("/signinOrganismo", ((SignInController) FactoryController.controller("signIn"))::renderSignInOrganismoDeControl);
            Server.app().get("/signinEntidad", ((SignInController) FactoryController.controller("signIn"))::renderSignInEntidadPrestadora);
            Server.app().post("/registrarUsuario", ((SignInController) FactoryController.controller("signIn"))::processSignInRedirect);


            Server.app().get("/admin/cargaEntidades",
                    ((EntidadesOrganismosController) FactoryController.controller("entidades"))::indexEntidades,
                    admin);
            Server.app().get("/admin/cargaOrganismos",
                    ((EntidadesOrganismosController) FactoryController.controller("organismos"))::indexOrganismos,
                    admin);
            Server.app().post("/admin/cargarCSVEntidades",
                    ((EntidadesOrganismosController) FactoryController.controller("entidades"))::cargarMasivaEntidades,
                    admin);
            Server.app().post("/admin/cargarCSVOrganismos",
                    ((EntidadesOrganismosController) FactoryController.controller("organismos"))::cargarMasivaOrganismos
                    ,admin);


            Server.app().get("/admin/usuarios",
                    ((UsuariosController) FactoryController.controller("usuarios"))::index
                    );
            Server.app().get("/admin/usuarios/{id}/editar",
                    ((UsuariosController) FactoryController.controller("usuarios"))::editar
                    );
            Server.app().post("/admin/editarUsuario/{id}",
                    ((UsuariosController) FactoryController.controller("usuarios"))::update
                    );
            Server.app().post("/eliminarUsuario/{id}",
                    ((UsuariosController) FactoryController.controller("usuarios"))::delete,
                    admin);


            Server.app().get("/admin/servicios", ((ServicioController) FactoryController.controller("servicios"))::indexServicios,
                    admin);
            Server.app().post("/admin/crearServicio", ((ServicioController) FactoryController.controller("servicios"))::crearServicio);


            Server.app().get("/rankings",
                    ((RankingController) FactoryController.controller("rankings"))::index,
                    admin, adminEntidad, adminOrganismo);
            Server.app().post("/generarRankings",
                    ((RankingController) FactoryController.controller("rankings"))::generarRanking,
                    adminEntidad, adminOrganismo);
            Server.app().get("/ranking/{id}",
                    ((RankingController) FactoryController.controller("rankings"))::ranking,
                    admin, adminEntidad, adminOrganismo);


            Server.app().get("/admin/incidentes",
                    ((IncidenteController) FactoryController.controller("incidentes"))::index,
                    admin);


            Server.app().get("/admin/roles", ((RolController) FactoryController.controller("rol"))::indexRoles
                    );
            Server.app().post("/admin/rol/crearRol", ((RolController) FactoryController.controller("rol"))::crearRol
                    );
            Server.app().post("/admin/rol/{id}/agregarPermiso", ((RolController) FactoryController.controller("rol"))::addPermiso,
                    admin);
            Server.app().post("/admin/rol/{id}/borrarPermiso/{permisoId}", ((RolController) FactoryController.controller("rol"))::borrarPermiso,
                    admin);

            Server.app().get("/admin/georef", ((GEOREFController) FactoryController.controller("georef"))::index,
                    admin);
            Server.app().post("/admin/georef", ((GEOREFController) FactoryController.controller("georef"))::actualizar
                    ,admin);


            Server.app().get("/admin/tipoentidad", ((EntidadController) FactoryController.controller("entidad"))::indexTipoEntidad
                    ,admin);
            Server.app().post("/admin/crearTipoEntidad", ((EntidadController) FactoryController.controller("entidad"))::crearTipoEntidad
                    ,admin);


            Server.app().get("/admin/entidades", ((EntidadController) FactoryController.controller("entidad"))::indexEntidades,
                    admin);

            Server.app().get("/admin/permisos", ((RolController) FactoryController.controller("rol"))::indexPermisos
                    ,admin);
            Server.app().post("/admin/crearPermiso", ((RolController) FactoryController.controller("rol"))::crearPermiso
                    ,admin);


            Server.app().get("/aperturaIncidentes", ((IncidenteController) FactoryController.controller("incidentes"))::aperturaIncidentes);
            Server.app().post("/aperturaIncidente", ((IncidenteController) FactoryController.controller("incidentes"))::abrirIncidente);
            Server.app().get("/incidentes", ((IncidenteController) FactoryController.controller("incidentes"))::indexIncidentes);
            Server.app().post("/cerrarIncidente/{id}", ((IncidenteController) FactoryController.controller("incidentes"))::cerrarIncidente);
            Server.app().get("/revisionIncidente/{id}", ((IncidenteController) FactoryController.controller("incidentes"))::revisionIncidente);


            Server.app().get("/comunidades", ((ComunidadController) FactoryController.controller("comunidad"))::indexComunidades);
            Server.app().get("/comunidades/crear", ((ComunidadController) FactoryController.controller("comunidad"))::indexCreacion);
            Server.app().post("/comunidades/crear", ((ComunidadController) FactoryController.controller("comunidad"))::crearComunidad);
            Server.app().post("/comunidad/delete/{id}", ((ComunidadController) FactoryController.controller("comunidad"))::eliminarComunidad);
            Server.app().get("/comunidad/{id}", ((ComunidadController) FactoryController.controller("comunidad"))::mostrarComunidad);
            Server.app().post("/comunidad/{comunidadId}/expulsar/{miembroId}", ((ComunidadController) FactoryController.controller("comunidad"))::expulsarMiembro);
            Server.app().post("/comunidad/{comunidadId}/ascender/{miembroId}", ((ComunidadController) FactoryController.controller("comunidad"))::ascenderAAdmin);
            Server.app().post("/comunidad/removerAdmin", ((ComunidadController) FactoryController.controller("comunidad"))::removerAdmin);
            Server.app().post("/comunidad/join/{comunidadId}", ((ComunidadController) FactoryController.controller("comunidad"))::joinComunidad);
            Server.app().post("/comunidad/disjoin/{comunidadId}", ((ComunidadController) FactoryController.controller("comunidad"))::disJoinComunidad);
            Server.app().post("/comunidad/addInteres", ((ComunidadController) FactoryController.controller("comunidad"))::addInteres);
            Server.app().post("/comunidad/removerInteres", ((ComunidadController) FactoryController.controller("comunidad"))::removerInteres);


            //Server.app().get("/perfil/{id}", ((PerfilController) FactoryController.controller("perfil"))::redirectPerfil);
            Server.app().get("/perfil", ((PerfilController) FactoryController.controller("perfil"))::perfilPropio); // MAS SEGURO
         /*   Server.app().get("/perfil/entidad/{id}", ((EntidadController) FactoryController.controller("entidad"))::indexEntidad);
            Server.app().get("/perfil/organismo/{id}", ((OrganismoDeControlController) FactoryController.controller("organismoDeControl"))::indexOrganismoDeControl);*/

            Server.app().post("/perfil/{id}/addLugarInteres", ((PerfilController) FactoryController.controller("perfil"))::addLugarDeInteres);
            Server.app().post("/perfil/{id}/removeLugarInteres/{idDireccion}", ((PerfilController) FactoryController.controller("perfil"))::removeLugarDeInteres);
            Server.app().post("/perfil/{id}/addService", ((PerfilController) FactoryController.controller("perfil"))::addServicioDeInteres);
            Server.app().post("/perfil/{id}/borrarServicio/{idServicio}", ((PerfilController) FactoryController.controller("perfil"))::borrarServicio);
            Server.app().post("/perfil/{idMiembro}/agregarHorario", ((PerfilController) FactoryController.controller("perfil"))::agregarHorario);
            Server.app().post("/perfil/{idMiembro}/borrarHorario", ((PerfilController) FactoryController.controller("perfil"))::borrarHorario);
            Server.app().post("/perfil/{idMiembro}/updateNotificationPreferences", ((PerfilController) FactoryController.controller("perfil"))::guardarMedioPreferido);


            Server.app().get("/entidad/{id}", ((EntidadController) FactoryController.controller("entidad"))::indexEntidad);
            Server.app().post("/entidad/{id}/crearEstablecimiento/{idEntidadPrestadora}", ((EntidadController) FactoryController.controller("entidad"))::crearEstablecimientoEntidadPrestadora);
            Server.app().post("/entidad/{id}/crearEstablecimiento", ((EntidadController) FactoryController.controller("entidad"))::crearEstablecimiento);
            Server.app().post("/entidades/crearEntidad", ((EntidadController) FactoryController.controller("entidad"))::crearEntidad);

            Server.app().get("/establecimiento/{id}", ((EstablecimientoController) FactoryController.controller("establecimiento"))::indexEstablecimiento);
            Server.app().post("/establecimiento/{id}/agregarServicio", ((EstablecimientoController) FactoryController.controller("establecimiento"))::addPrestacion);
        });

        //  Server.app().get("/entidadesPrestadoras", new EntidadesPrestadorasController()::index);



    }

    private static boolean isLoggedIn(Context ctx) {
        // Verificar la presencia de la cookie de autenticación u otros indicadores de inicio de sesión
        return ctx.cookie("id") != null;
    }

}
