package domain.rankings;
import domain.Repositorios.RepositorioIncidente;
import domain.Repositorios.RepositorioLeaderBoard;
import domain.Usuarios.Comunidades.Comunidad;
import domain.Usuarios.Comunidades.Miembro;
//import domain.repositorios.RepositorioComunidades;
import domain.entidades.Entidad;
import domain.localizaciones.Direccion;
import domain.rankings.Leaderboard.LeaderBoardType;
import domain.rankings.Leaderboard.Leaderboard;
import domain.rankings.Leaderboard.RankLeaderBoardUnit;
import domain.services.georef.entities.Localidad;
import domain.services.notificadorDeIncidentes.CommandoNotificacion;
import domain.servicios.PrestacionDeServicio;
import domain.servicios.Servicio;
import domain.informes.Incidente;

import java.util.*;

public class RankingMayorGradoImpacto implements Ranking {
    private int CTF = 2;
    @Override
    public Leaderboard generarRanking() {
       /* // Calcular el grado de impacto por entidad
        Map<String, Integer> gradoImpactoPorEntidad = calcularGradoImpactoPorEntidad(incidentes);

        // Crear una lista de entidades con su grado de impacto
        List<EntidadGradoImpacto> rankingSemana = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : gradoImpactoPorEntidad.entrySet()) {
            String entidad = entry.getKey();
            int gradoImpacto = entry.getValue();
            rankingSemana.add(new EntidadGradoImpacto(entidad, gradoImpacto));
        }

        // Ordenar el ranking por grado de impacto descendente
        rankingSemana.sort(Comparator.comparing(EntidadGradoImpacto::getGradoImpacto).reversed());

        // Imprimir el ranking semanal
        System.out.println("ranking.hbs semanal por mayor grado de impacto:");
        int posicion = 1;
        for (EntidadGradoImpacto entidadGrado : rankingSemana) {
            System.out.println(posicion + ". " + entidadGrado.getEntidad() + " - Grado de impacto: " + entidadGrado.getGradoImpacto());
            posicion++;
        }*/
        List<Incidente> incidentes = new RepositorioIncidente().findOpenedLastWeek();
        Map<Entidad, Double> gradoDeImpactoPorEntidad = this.calcularGradoImpactoPorEntidad(incidentes);
        List<RankLeaderBoardUnit> rankLeaderBoardUnitList = new ArrayList<>();
        gradoDeImpactoPorEntidad.keySet().forEach(
                E ->
                        rankLeaderBoardUnitList.add(new RankLeaderBoardUnit(E.getId(), gradoDeImpactoPorEntidad.get(E)))
                );
        Leaderboard leaderboard = new Leaderboard(rankLeaderBoardUnitList, LeaderBoardType.MAYOR_GRADO_IMPACTO);
        new RepositorioLeaderBoard().save(leaderboard);
        return leaderboard;
    }

    private Map<Entidad, Double> calcularGradoImpactoPorEntidad(List<Incidente> incidentes) {
        Map<Entidad, Double> gradoImpactoPorEntidad = new HashMap<>();

        for (Incidente incidente : incidentes) {
            Entidad entidad = incidente.getServicioAfectado().getEstablecimiento().getEntidad();
            Double impacto = this.CalcularGradoDeImacto(incidente);

            gradoImpactoPorEntidad.put(entidad, gradoImpactoPorEntidad.getOrDefault(entidad, 0.0d) + impacto);
        }
        return gradoImpactoPorEntidad;
    }

    private Double CalcularGradoDeImacto(Incidente incidente)
    {
        List<Miembro> miembrosAfectados = RepositorioIncidente.findUniqueMembersFromCommunitiesAffectedByIncident(incidente);
        if (incidente.estaAbierto())
            return (double)miembrosAfectados.size()*CTF;
        else
            return (double)miembrosAfectados.size()* incidente.getTiempoDeResolucion();
    }
}
/*
        for (Incidente incidente : incidentes) {
            String entidad = "TODO: here";//incidente.getServicioAfectado().getLocalizacion().getEntidad().getNombre();
            int miembrosComunidad = obtenerMiembrosComunidad(incidente);

            if (gradoImpactoPorEntidad.containsKey(entidad)) {
                int gradoImpactoExistente = gradoImpactoPorEntidad.get(entidad);
                gradoImpactoPorEntidad.put(entidad, gradoImpactoExistente + miembrosComunidad);
            } else {
                gradoImpactoPorEntidad.put(entidad, miembrosComunidad);
            }
        }

        return gradoImpactoPorEntidad;
    }


    private int obtenerMiembrosComunidad(Incidente incidente) { //No se si hay q filtrar comunidades x lugar
        PrestacionDeServicio servicioAfectado = incidente.getServicioAfectado();
        Direccion lugarAfectado = servicioAfectado.getLocalizacion();
        Comunidad comunidad = new Comunidad();//RepositorioComunidades.getInstance().obtenerComunidadPorLugar(lugarAfectado);

        if (comunidad != null) {
            List<Miembro> miembrosFiltrados = comunidad.miembrosFiltradosPorInteresEnLocalizacion(lugarAfectado);
            return miembrosFiltrados.size();
        }

        return 0;
    }

    public class EntidadGradoImpacto {
        private String entidad;
        private int gradoImpacto;

        public EntidadGradoImpacto(String entidad, int gradoImpacto) {
            this.entidad = entidad;
            this.gradoImpacto = gradoImpacto;
        }

        public String getEntidad() {
            return entidad;
        }

        public int getGradoImpacto() {
            return gradoImpacto;
        }
    }
}*/