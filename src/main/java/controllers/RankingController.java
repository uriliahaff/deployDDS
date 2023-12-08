package controllers;

import domain.Repositorios.RepositorioEntidadPrestadoraOrganismoControl;
import domain.Repositorios.RepositorioLeaderBoard;
import domain.Repositorios.RepositorioUsuario;
import domain.Usuarios.Rol;
import domain.Usuarios.Usuario;
import domain.rankings.Leaderboard.LeaderBoardType;
import domain.rankings.Leaderboard.Leaderboard;
import domain.rankings.Leaderboard.RankLeaderBoardUnit;
import domain.rankings.RankingMayorCantidadIncidentesReportados;
import domain.rankings.RankingMayorGradoImpacto;
import domain.rankings.RankingMayorPromedioTiempoCierreIncidentes;
import domain.services.NavBarVisualizer;
import io.javalin.http.Context;

import java.util.*;

public class RankingController {
    private RepositorioUsuario repositorioUsuario;
    private RepositorioLeaderBoard repositorioLeaderBoard = new RepositorioLeaderBoard();


    public RankingController(RepositorioUsuario repoUsuario){
        this.repositorioUsuario = repoUsuario;
    }

    private List<RankLeaderBoardUnit> getAndSortRanking(LeaderBoardType type) {
        List<RankLeaderBoardUnit> rankList = getLastRanking(type);
        rankList.sort(Comparator.comparing(RankLeaderBoardUnit::getValue).reversed());

        for (int i = 0; i < rankList.size(); i++) {
            RankLeaderBoardUnit rankUnit = rankList.get(i);
            rankUnit.setPosicion(i+1);
        }

        return rankList;
    }

    public void index(Context context){
        Map<String, Object> model = new HashMap<>();

        model.put("username", context.cookie("username"));

        model.put("rankingPromedioTiempo", getAndSortRanking(LeaderBoardType.MAYOR_PROMEDIO_TIEMPO));
        model.put("rankingMayorImpacto", getAndSortRanking(LeaderBoardType.MAYOR_GRADO_IMPACTO));
        model.put("rankingMayorIncidentesReportados", getAndSortRanking(LeaderBoardType.MAYORCANTIDADINCIDENTESREPORTADOS));

        Usuario user = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));
        NavBarVisualizer navBarVisualizer = new NavBarVisualizer();

        navBarVisualizer.colocarItems(user.getRoles(), model);

        model.put("admin", user.getRoles().stream().anyMatch(rol -> "admin".equals(rol.getNombre())));

        context.render("rankings.hbs", model);
    }
    private List<RankLeaderBoardUnit> getLastRanking(LeaderBoardType type)
    {
        Leaderboard leaderboard = repositorioLeaderBoard
                .findLatestLeaderboardByType(type);
        if(leaderboard != null)
            return leaderboard.getRankLeaderBoardUnits();
        return new ArrayList<RankLeaderBoardUnit>();
    }
    public void ranking(Context context){
        Map<String, Object> model = new HashMap<>();

        int rankings = Integer.parseInt(context.pathParam("id"));

        switch (rankings) {
            case 1:
                model.put("ranking", getAndSortRanking(LeaderBoardType.MAYOR_PROMEDIO_TIEMPO));
                model.put("descripcion", "Entidades con mayor promedio de tiempo de cierre de incidentes (diferencia entre horario de cierre de incidente y horario de apertura) en la semana");
                break;
            case 2:
                model.put("ranking", getAndSortRanking(LeaderBoardType.MAYOR_GRADO_IMPACTO));
                model.put("descripcion", "Entidades con mayor cantidad de incidentes reportados en la semana.");
                break;
            case 3:
                model.put("ranking", getAndSortRanking(LeaderBoardType.MAYORCANTIDADINCIDENTESREPORTADOS));
                model.put("descripcion", "Mayor grado de impacto de las problemáticasa.");

                break;
        }

        model.put("username", context.cookie("username"));
        Usuario user = repositorioUsuario.findUsuarioById(Integer.parseInt(context.cookie("id")));
        NavBarVisualizer navBarVisualizer = new NavBarVisualizer();
        navBarVisualizer.colocarItems(user.getRoles(), model);
        context.render("ranking.hbs", model);
    }

    public  void generarRanking(Context context)
    {
        new RankingMayorCantidadIncidentesReportados().generarRanking();
        new RankingMayorGradoImpacto().generarRanking();
        new RankingMayorPromedioTiempoCierreIncidentes().generarRanking();
        context.redirect("/rankings");

    }



}
