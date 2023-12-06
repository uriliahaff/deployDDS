package domain.Repositorios;

import domain.informes.NotificacionDiferida;
import domain.other.EntityManagerProvider;

import javax.persistence.EntityManager;

public class RepositorioNotificacionDiferida
{
    private static EntityManager entityManager = EntityManagerProvider.getInstance().getUniqueEntityManager();

    public static EntityManager getEntityManager()
    {
        return entityManager;
    }
    public void save(NotificacionDiferida notificacionDiferida)
    {
        //if (!entityManager.getTransaction().isActive())
        entityManager.getTransaction().begin();
        entityManager.persist(notificacionDiferida);
        entityManager.getTransaction().commit();
    }
}
