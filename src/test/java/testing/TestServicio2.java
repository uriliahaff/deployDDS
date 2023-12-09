package testing;

import domain.Procesos.ComunidadManager;
import domain.Procesos.EntidadesManager;
import domain.Procesos.EstablecimientoManager;
import domain.Repositorios.*;
import domain.Usuarios.Comunidades.Comunidad;
import domain.Usuarios.Comunidades.ConfiguracionNotificacionDeIncidentes;
import domain.Usuarios.Comunidades.Miembro;
import domain.Usuarios.Usuario;
import domain.entidades.Entidad;
import domain.entidades.Establecimiento;
import domain.entidades.TipoEntidad;
import domain.informes.Incidente;
import domain.localizaciones.Direccion;
import domain.services.georef.entities.Localidad;
import domain.services.georef.entities.Municipio;
import domain.services.georef.entities.Provincia;
import domain.services.service2.CalculadorConfianzaAPI;
import domain.services.service2.CalculadorConfianzaAPIService;
import domain.services.service2.CalculadorConfianzaAdapter;
import domain.services.service2.CambioDePuntaje;
import domain.servicios.PrestacionDeServicio;
import domain.servicios.Servicio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TestServicio2
{
    private Comunidad comunidad1;


    @Test
    public void testActualizarComunidadValues() throws IOException {
        CalculadorConfianzaAdapter calculadorConfianzaAdapter = CalculadorConfianzaAdapter.getInstance();
        List<Comunidad> comunidades = new ArrayList<>();
        comunidades.add(new RepositorioComunidad().findByName("com 4"));
        System.out.println("Comunidad: "+comunidades.get(0).getMiembros().get(0).getUsuario().getGradoDeConfianza());
        System.out.println(comunidades.get(0).getMiembros().stream().map(Miembro::getUsuario).mapToDouble(Usuario::getGradoDeConfianza).sum());
        calculadorConfianzaAdapter.actualizarGradoConfianzaDeComunidades(comunidades);
        Assertions.assertEquals(-1,comunidades.get(0).getGradoDeConfianza());
    }
}
