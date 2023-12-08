package domain.rankings.Leaderboard;

import domain.Repositorios.RepositorioEntidad;
import domain.entidades.Entidad;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "rank_leaderboard")
public class RankLeaderBoardUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "leaderboard_id")
    private Leaderboard leaderboard;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "entidad_id")
    private Entidad entidad;

    @Column(name = "value")
    private double value;

    @Transient
    @Setter
    @Getter
    private int posicion;

    @Column(name = "date_time")
    private LocalDate dateTime;

    public RankLeaderBoardUnit(){}

    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    public Entidad getEntidad() {
        return entidad;
    }

    public double getValue() {
        return value;
    }

    public LocalDate getDateTime() {
        return dateTime;
    }

    public RankLeaderBoardUnit(Integer entidad_id, double value) {
        this.entidad = new RepositorioEntidad().findEntidadById(entidad_id);
        //this.entidad_name = this.entidad.getNombre();
        this.value = value;
        dateTime = LocalDate.now();
    }

    public int getId() {
        return id;
    }

    public void setLeaderboard(Leaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }
}
