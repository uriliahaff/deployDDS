package domain.Repositorios;

import domain.Usuarios.Usuario;
import domain.entidades.Entidad;
import domain.entidades.Establecimiento;
import domain.informes.Incidente;
import domain.other.EntityManagerProvider;
import domain.servicios.PrestacionDeServicio;
import domain.servicios.Servicio;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

public class RepositorioServicio
{
    private static EntityManager entityManager = EntityManagerProvider.getInstance().getEntityManager();

    public void save(Servicio servicio) {
        entityManager.getTransaction().begin();
        entityManager.persist(servicio);
        entityManager.getTransaction().commit();
    }

    public void update(Servicio servicio) {
        entityManager.getTransaction().begin();
        entityManager.merge(servicio);
        entityManager.getTransaction().commit();
    }
    public List<Servicio> findAll()
    {
        TypedQuery<Servicio> query = entityManager.createQuery("SELECT s FROM Servicio s", Servicio.class);
        return query.getResultList();
    }

    public void delete(Servicio servicio) {
    entityManager.getTransaction().begin();
    entityManager.remove(servicio);
    entityManager.getTransaction().commit();
}

    public void delete(int id) {
        entityManager.getTransaction().begin();
        Entidad entidad = entityManager.find(Entidad.class, id);
        if (entidad != null) {
            entityManager.remove(entidad);
        }
        entityManager.getTransaction().commit();
    }

    public static List<Servicio> findServicioByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        TypedQuery<Servicio> query = entityManager.createQuery(
                "SELECT u FROM Servicio u WHERE u.id IN :ids", Servicio.class
        );
        query.setParameter("ids", ids);
        return query.getResultList();
    }

    public Servicio findServicioById(int id) {
        return entityManager.find(Servicio.class, id);
    }
    public PrestacionDeServicio findPrestacionById(int id) {
        return entityManager.find(PrestacionDeServicio.class, id);
    }

    public List<PrestacionDeServicio> buscarTodasLasPrestaciones() {
        return entityManager.createQuery("from " + PrestacionDeServicio.class.getName()).getResultList();
    }

    public Servicio findServicioByName(String nombre) {
        try {
            return entityManager.createQuery("SELECT u FROM Servicio u WHERE u.nombre = :nombre", Servicio.class)
                    .setParameter("nombre", nombre)
                    .setMaxResults(1)
                    .getSingleResult();

        } catch (NoResultException e) {
            // Manejar el caso en que no se encuentra ningún usuario con el nombre de usuario dado
            return null;
        }
    }

}
