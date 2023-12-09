package domain.other;

import javax.persistence.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

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
            emf = createEntityManagerFactory();
        }
        if (em == null || !em.isOpen()) {
            em = emf.createEntityManager();
        }
        return em;
    }

    public static EntityManagerFactory createEntityManagerFactory() {
        Map<String, String> env = System.getenv();
        Map<String, Object> configOverrides = new HashMap<String, Object>();

        String[] keys = new String[] {
                "hibernate.connection.url",
                "hibernate.connection.username",
                "hibernate.connection.password",
                "hibernate.connection.driver_class",
                "hibernate.hbm2ddl.auto",
                "hibernate.connection.pool_size",
                "hibernate.show_sql" };

        for (String key : keys) {
            if (env.containsKey(key)) {

                String value = env.get(key);
                configOverrides.put(key, value);

            }
        }
        return Persistence.createEntityManagerFactory("db", configOverrides);
    }
    public synchronized EntityManager getUniqueEntityManager()
    {
        return emf.createEntityManager();
    }

}
