package domain.informes;

import domain.Repositorios.RepositorioNotificacionDiferida;
import domain.Usuarios.Comunidades.ConfiguracionNotificacionDeIncidentes;
import domain.Usuarios.Comunidades.Miembro;
import domain.other.EntityManagerProvider;
import domain.services.notificadorDeIncidentes.NotificadorDeIncidentes;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class CronNotificadorDiferido {
    private static CronNotificadorDiferido instance = null;
    private final Timer timer = new Timer();
    private final Queue<NotificacionDiferida> colaDeNotificaciones = new LinkedList<>();

    private CronNotificadorDiferido() {
        // Tarea que se ejecutará cada 5 minutos
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                executeAndDeleteExpiredNotifications();
            }
        };
        timer.scheduleAtFixedRate(repeatedTask, 0, 300000);
    }

    public static CronNotificadorDiferido getInstance() {
        if (instance == null) {
            instance = new CronNotificadorDiferido();
        }
        return instance;
    }

    public synchronized void agregarNotificacion(Incidente incidente, Miembro miembro) {
        String texto = incidente.getDescripcion();
        String titulo = "Al servicio " + incidente.getServicioAfectado().getServicio().getNombre() + " le paso algo";

        ConfiguracionNotificacionDeIncidentes config = miembro.getConfiguracionNotificacionDeIncidentes();
        NotificacionDiferida notificacionDiferida = new NotificacionDiferida(
                texto, titulo, miembro, getNextScheduledDate(config.getHorarioPreferencia()));

        colaDeNotificaciones.add(notificacionDiferida);
        procesarCola();
    }

    private void procesarCola() {
        while (!colaDeNotificaciones.isEmpty()) {
            NotificacionDiferida notificacion = colaDeNotificaciones.poll();
            new RepositorioNotificacionDiferida().save(notificacion);
        }
    }

    public LocalDateTime getNextScheduledDate(List<Float> horarios) {
        LocalDateTime now = LocalDateTime.now();
        float nowAsFloat = now.getHour() + now.getMinute() / 60f;

        List<Float> horariosPostNow = horarios.stream()
                .filter(h -> h > nowAsFloat)
                .sorted()
                .collect(Collectors.toList());

        if (!horariosPostNow.isEmpty()) {
            return convertToLocalDate(now, horariosPostNow.get(0));
        } else {
            return convertToLocalDate(now.plusDays(1), horarios.stream()
                    .min(Float::compare)
                    .orElseThrow());
        }
    }

    public void executeAndDeleteExpiredNotifications() {

        EntityManager em = EntityManagerProvider.getInstance().getUniqueEntityManager();

        em.getTransaction().begin();

        TypedQuery<NotificacionDiferida> query = em.createQuery(
                "SELECT n FROM NotificacionDiferida n WHERE n.fechaDeEnvio < :now", NotificacionDiferida.class);

        query.setParameter("now", LocalDateTime.now());

        List<NotificacionDiferida> expiredNotifications = query.getResultList();

        for (NotificacionDiferida notificacion : expiredNotifications) {
            notificacion.run();
            em.remove(notificacion);
        }

        em.getTransaction().commit();
    }

    private LocalDateTime convertToLocalDate(LocalDateTime date, float timeAsFloat) {
        int totalMinutes = (int) timeAsFloat;
        int hour = totalMinutes / 60;
        int minute = totalMinutes % 60;
        return LocalDateTime.of(date.toLocalDate(), LocalTime.of(hour, minute));
    }

}

/*
    //En este código, primero obtenemos el tiempo actual y lo convertimos a formato float.
    // Luego, filtramos los horarios que son posteriores a la hora actual y los ordenamos.
    // Si hay horarios posteriores a la hora actual, devolvemos el primer horario de la lista ordenada (es decir, el más próximo a la hora actual).
    // Si no hay horarios posteriores a la hora actual, devolvemos el horario más temprano del día siguiente.
    public int getNextScheduledTime(List<Float> horarios) {
        LocalTime now = LocalTime.now();

        float nowAsFloat = now.getHour() + now.getMinute() / 60f;

        List<Float> horariosPostNow = horarios.stream()
                .filter(h -> h > nowAsFloat)
                .sorted()
                .collect(Collectors.toList());

        if (!horariosPostNow.isEmpty()) {
            return convertToMinutes(horariosPostNow.get(0)) - now.toSecondOfDay() / 60;
        } else {
            int minutesUntilEndOfDay = 1440 - now.toSecondOfDay() / 60;
            return minutesUntilEndOfDay + convertToMinutes(horarios.stream()
                    .min(Float::compare)
                    .orElseThrow());
        }
    }


    private int convertToMinutes(float time) {
        int hours = (int) time;
        int minutes = Math.round((time - hours) * 60);
        return hours * 60 + minutes;
    }*/

