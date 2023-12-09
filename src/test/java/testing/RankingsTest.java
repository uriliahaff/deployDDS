package testing;

import domain.Repositorios.*;
import domain.Usuarios.Comunidades.Comunidad;
import domain.Usuarios.Comunidades.Miembro;
import domain.Usuarios.Usuario;
import domain.entidades.Entidad;
import domain.entidades.Establecimiento;
import domain.informes.Incidente;
import domain.localizaciones.Direccion;
import domain.rankings.Leaderboard.Leaderboard;
import domain.rankings.RankingMayorCantidadIncidentesReportados;
import domain.rankings.RankingMayorPromedioTiempoCierreIncidentes;
import domain.services.georef.entities.Municipio;
import domain.services.georef.entities.Provincia;
import domain.servicios.Estado;
import domain.servicios.PrestacionDeServicio;
import domain.servicios.Servicio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RankingsTest {


    private Miembro miembro1, miembro2,miembro3,miembro4;
    private Usuario usuario1, usuario2, usuario3, usuario4;
    private Establecimiento establecimiento1, establecimiento2, establecimiento3;
    private Entidad entidad1, entidad2,entidad3;
    private PrestacionDeServicio prestacion1, prestacion2, prestacion3, prestacion4;
    private Incidente incidente1, incidente2, incidente3, incidente4;
    private Comunidad comunidad1, comunidad2;




    @Test
    public void rankingTest1()
    {
        List<Incidente> incidentes = new ArrayList<Incidente>();
        incidentes.add(incidente1);
        incidentes.add(incidente2);
        incidentes.add(incidente3);
        incidentes.add(incidente4);

        Leaderboard leaderbord = new RankingMayorCantidadIncidentesReportados().generarRanking();
        Assertions.assertTrue(leaderbord.getRankLeaderBoardUnits().size()>0);

    }
    @Test
    public void rankingTest2()
    {
        Leaderboard leaderbord = new RankingMayorPromedioTiempoCierreIncidentes().generarRanking();
        Assertions.assertTrue(leaderbord.getRankLeaderBoardUnits().size()>0);

    }
}
