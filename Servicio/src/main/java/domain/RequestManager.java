package domain;

import io.javalin.Javalin;

import java.util.ArrayList;
import java.util.List;

public class RequestManager
{
    public static void main(String[] args)
    {
        Integer port = Integer.parseInt(System.getProperty("port", "8082"));
        Javalin app = Javalin.create().start(port);
        app.get("/",ctx -> ctx.result("Hola Mundo") );

        app.post("/calcularGradosDeConfianza", ctx -> {
            List<Incidente> incidentes = ctx.bodyAsClass(List.class); // Suponiendo que el cuerpo del mensaje es una lista de Incidentes en formato JSON
            List<CambioDePuntaje> cambios = CalculadorDeCambiosGradosDeConfianza.calcularGradosdeConfianza(incidentes);
            ctx.json(cambios);
        });
        app.post("/actualizarGradoConfianzaDeComunidades", ctx -> {
        List<Comunidad> comunidades = ctx.bodyAsClass(List.class); // Suponiendo que el cuerpo del mensaje es una lista de Incidentes en formato JSON
        //List<Comunidad> cambios = CalculadorDeCambiosGradosDeConfianza.calcularGradosdeConfianza(incidentes);
            CalculadorDeCambiosGradosDeConfianza.actualizarGradoConfianzaDeComunidades(comunidades);
        ctx.json(comunidades);
    });

    }

}
