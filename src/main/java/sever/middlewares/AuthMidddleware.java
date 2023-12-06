package sever.middlewares;

import domain.Repositorios.RepositorioUsuario;
import domain.Usuarios.Rol;
import domain.Usuarios.Usuario;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.security.RouteRole;
import sever.exceptions.NoAuthExcpetion;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AuthMidddleware {


    public static void aply(JavalinConfig config) {
        List<String> listaPermitidos = Arrays.asList(
                "login"
                ,"logout"
                ,"signin"
                ,"registrarUsuario"
                ,"signin"
                ,"signinOrganismo"
                ,"signinEntidad"
        );
        config.accessManager(((handler, context, routeRoles) -> {
            if (context.cookie("id") == null && !listaPermitidos.stream().anyMatch(permits -> context.path().equals("/"+permits)))
            {//!context.path().equals("/login")&& !context.path().equals("/signin")) {
                throw new NoAuthExcpetion();

            } else if (context.cookie("id") != null && context.path().equals("/login")) {
                context.redirect("/");
            }

            List<Rol> userRoles = getUserRoleType(context);
            if (routeRoles.size() == 0 || contiene(userRoles, routeRoles) ) {
                handler.handle(context);
            } else {
                throw new NoAuthExcpetion();
            }


        }));

    }

    private static List<Rol> getUserRoleType(Context ctx) {
        String userIdCookie = ctx.cookie("id");
        if (userIdCookie != null && !userIdCookie.isEmpty()) {
            RepositorioUsuario repo = new RepositorioUsuario();
            Usuario user = repo.findUsuarioById(Integer.parseInt(userIdCookie));
            return user.getRoles();
        }
        // Manejo adecuado cuando la cookie "id" es nula o vacía
        return Collections.emptyList(); // o null, dependiendo de tu lógica
    }
    public static boolean contiene(List<Rol> lista1, Set<? extends RouteRole> lista2) {
        for (Rol rol1 : lista1) {
            for (RouteRole rol2 : lista2) {
                if (rol2 instanceof Rol && rol1.getNombre().equals(((Rol) rol2).getNombre())) {
                    return true; // Se encontró un nombre de rol igual
                }
            }
        }

        return false; // No se encontraron nombres de rol iguales
    }
}