package domain.other;

import javax.persistence.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerProvider {

    private static final EntityManagerProvider instance = new EntityManagerProvider();
    private static EntityManagerFactory emf;
    private static EntityManager em;

    private EntityManagerProvider() {
        // Private constructor to restrict new instances
    }

    public static EntityManagerProvider getInstance() {
        return instance;
    }

    public synchronized EntityManager getEntityManager() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("simple-persistence-unit");
        }
        if (em == null || !em.isOpen()) {
            em = emf.createEntityManager();
        }
        return em;//emf.createEntityManager();
    }
    public synchronized EntityManager getUniqueEntityManager()
    {
        return emf.createEntityManager();
    }

}
