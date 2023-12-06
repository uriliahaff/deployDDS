package domain.Repositorios;

import domain.Usuarios.*;
import domain.Usuarios.Comunidades.Miembro;
import domain.entidades.TipoEntidad;
import domain.other.EntityManagerProvider;
import domain.servicios.Servicio;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RepositorioUsuario {

    private static EntityManager entityManager = EntityManagerProvider.getInstance().getEntityManager();;


    /*public RepositorioUsuario() {
        this.entityManager = EntityManagerProvider.getInstance().getEntityManager();
    }*/

    // Métodos generales para guardar y actualizar
    private <T> void saveEntity(T entity) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(entity);
        transaction.commit();
    }


    public List<OrganismoDeControl> findAllOrganismoDeControl() {
        return entityManager.createQuery("SELECT o FROM OrganismoDeControl o", OrganismoDeControl.class)
                .getResultList();
    }
    public List<EntidadPrestadora> findAllEntidadPrestadora() {
        return entityManager.createQuery("SELECT e FROM EntidadPrestadora e", EntidadPrestadora.class)
                .getResultList();
    }


    public OrganismoDeControl findOrganismoDeControlByUserId(int userId) {
        try {
            TypedQuery<OrganismoDeControl> query = entityManager.createQuery(
                    "SELECT o FROM OrganismoDeControl o WHERE o.usuario.id = :userId", OrganismoDeControl.class);
            query.setParameter("userId", userId);
            return query.getSingleResult(); // Obtiene un único resultado
        } catch (NoResultException e) {
            return null; // Retorna null si no se encuentra ningún resultado
        }
    }

    public EntidadPrestadora findEntidadPrestadoraByUserId(int userId) {
        try {
            TypedQuery<EntidadPrestadora> query = entityManager.createQuery(
                    "SELECT e FROM EntidadPrestadora e WHERE e.usuario.id = :userId", EntidadPrestadora.class);
            query.setParameter("userId", userId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // O manejar de otra manera si no hay resultados
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException("Más de una EntidadPrestadora encontrada para userId: " + userId);
        }
    }



    public void actualizar(Object o) {
        EntityTransaction tx = entityManager.getTransaction();
        if(!tx.isActive())
            tx.begin();
        entityManager.merge(o);
        tx.commit();
    }


    public <T> T findById(Class<T> clazz, int id) {
        return entityManager.find(clazz, id);
    }
    public Usuario findUsuarioById(int id) {
        return entityManager.find(Usuario.class, id);
    }
    public Usuario findUsuarioByUsername(String username) {
        try {
            return entityManager.createQuery("SELECT u FROM Usuario u WHERE u.username = :username", Usuario.class)
                    .setParameter("username", username)
                    .setMaxResults(1)
                    .getSingleResult();

        } catch (NoResultException e) {
            // Manejar el caso en que no se encuentra ningún usuario con el nombre de usuario dado
            return null;
        }
    }
    public Usuario findUsuarioByUsernameAndPassword(String username, String password) {
        try {
            return entityManager
                    .createQuery("SELECT u FROM Usuario u WHERE u.username = :username AND u.password = :password", Usuario.class)
                    .setParameter("username", username)
                    .setParameter("password", Usuario.hashPassword(password))
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            // Manejar el caso en que no se encuentra ningún usuario con el nombre de usuario y contraseña dados
            return null;
        }
    }


    public Miembro findMiembroByUsuarioId(int usuarioId) {
        try {
            return entityManager
                    .createQuery("SELECT m FROM Miembro m WHERE m.usuario.id = :usuarioId", Miembro.class)
                    .setParameter("usuarioId", usuarioId)
                    .getSingleResult();
        } catch (NoResultException e) {
            // Manejar el caso en que no se encuentra ningún miembro con el ID de usuario dado
            return null;
        }
    }
    public Miembro findMiembroById(int id) {
        return entityManager.find(Miembro.class, id);
    }

    public OrganismoDeControl findOrganismoDeControlById(int id) {
        return entityManager.find(OrganismoDeControl.class, id);
    }



    public EntidadPrestadora findEntidadPrestadoraById(int id) {
        return entityManager.find(EntidadPrestadora.class, id);
    }

    // Métodos específicos que usan los métodos generales
    public void saveUsuario(Usuario usuario) {
        saveEntity(usuario);
    }

    public void updateUsuario(Usuario usuario) {
        actualizar(usuario);
    }

    public void saveMiembro(Miembro miembro) {
        saveEntity(miembro);
    }

    public void updateMiembro(Miembro miembro) {
        actualizar(miembro);
    }

    public void saveOrganismoDeControl(OrganismoDeControl organismoDeControl) {
        saveEntity(organismoDeControl);
    }
    public void saveOrganismosDeControl(List<OrganismoDeControl> organismosDeControl) {
        entityManager.getTransaction().begin();

        try {
            int i = 0;
            for (OrganismoDeControl organismo : organismosDeControl) {
                entityManager.persist(organismo);

                if (i % 20 == 0) { // Flush and clear in batches
                    entityManager.flush();
                    entityManager.clear();
                }
                i++;
            }

            entityManager.getTransaction().commit();
        } catch (RuntimeException e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e; // or handle it appropriately
        }
    }
    public void saveEntidadesPrestadoras(List<EntidadPrestadora> entidadesPrestadoras) {
        entityManager.getTransaction().begin();

        try {
            int i = 0;
            for (EntidadPrestadora entidadPrestadora : entidadesPrestadoras) {
                entityManager.persist(entidadPrestadora);

                if (i % 20 == 0) { // Flush and clear in batches
                    entityManager.flush();
                    entityManager.clear();
                }
                i++;
            }

            entityManager.getTransaction().commit();
        } catch (RuntimeException e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e; // or handle it appropriately
        }
    }

    public void updateOrganismoDeControl(OrganismoDeControl organismoDeControl) {
        actualizar(organismoDeControl);
    }

    public void saveEntidadPrestadora(EntidadPrestadora entidadPrestadora) {
        saveEntity(entidadPrestadora);
    }

    public void updateEntidadPrestadora(EntidadPrestadora entidadPrestadora) {
        actualizar(entidadPrestadora);
    }

    public void delete(Usuario usuario) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();


            // Intentamos eliminar la entidad asociada correspondiente
            if (
                    tryDeleteEntity(Miembro.class, "Miembro", usuario.getId()) ||
                    tryDeleteEntity(OrganismoDeControl.class, "OrganismoDeControl", usuario.getId()) ||
                    tryDeleteEntity(EntidadPrestadora.class, "EntidadPrestadora", usuario.getId())
            ) {
                // Se eliminó alguna entidad asociada, podemos continuar
            }

            entityManager.remove(usuario);

        transaction.commit();
    }

    private <T> boolean tryDeleteEntity(Class<T> clazz, String className, int userId) {
        TypedQuery<T> query = entityManager.createQuery("SELECT e FROM " + className + " e WHERE e.usuario.id = :id", clazz);
        query.setParameter("id", userId);
        T entity = query.getResultStream().findFirst().orElse(null);
        if (entity != null) {
            entityManager.remove(entity);
            return true;
        }
        return false;
    }

    public List buscarTodosUsuarios() {
        return entityManager.createQuery("from " + Usuario.class.getName()).getResultList();
    }

    public List<Rol> buscarTodosRoles() {
        return entityManager.createQuery("from " + Rol.class.getName()).getResultList();
    }

    public List<Permiso> findAllPermisos() {
        return entityManager.createQuery("SELECT p FROM Permiso p", Permiso.class).getResultList();
    }

    public static List<Miembro> findMiembrosByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        TypedQuery<Miembro> query = entityManager.createQuery(
                "SELECT u FROM Miembro u WHERE u.id IN :ids", Miembro.class
        );
        query.setParameter("ids", ids);
        return query.getResultList();
    }

    public static List<Miembro> findMiembrosByUserId(Integer idUsuario) {
        /*List<Miembro> miembros = entityManager.createQuery("from " + Miembro.class.getName()).getResultList();

        List<Miembro> miembrosDelUsuario = miembros.stream()
                .filter(miembro -> miembro.getUsuario().getId() == idUsuario)
                .collect(Collectors.toList());
*/
        String jpql = "SELECT m FROM Miembro m WHERE m.usuario.id = :idUsuario";
        List<Miembro> miembrosDelUsuario = entityManager.createQuery(jpql, Miembro.class)
                .setParameter("idUsuario", idUsuario)
                .getResultList();
        return miembrosDelUsuario;

    }
}
