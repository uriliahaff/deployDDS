package domain.Repositorios;

import domain.Usuarios.OrganismoDeControl;
import domain.localizaciones.Direccion;
import domain.other.EntityManagerProvider;
import domain.services.georef.entities.Localidad;
import domain.services.georef.entities.Municipio;
import domain.services.georef.entities.Provincia;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

public class RepositorioDireccion
{

    private static EntityManager entityManager = EntityManagerProvider.getInstance().getEntityManager();

    // Métodos para Dirección
    public void saveDireccion(Direccion direccion) {
        entityManager.getTransaction().begin();
        entityManager.persist(direccion);
        entityManager.getTransaction().commit();
    }

    public void updateDireccion(Direccion direccion) {
        entityManager.getTransaction().begin();
        entityManager.merge(direccion);
        entityManager.getTransaction().commit();
    }

    public void deleteDireccion(Direccion direccion) {
        entityManager.getTransaction().begin();
        entityManager.remove(direccion);
        entityManager.getTransaction().commit();
    }

    public Direccion findDireccion(int id)
    {
        return entityManager.find(Direccion.class, id);
    }

    public List<Direccion> findAllDirecciones() {
        return entityManager.createQuery("SELECT d FROM Direccion d", Direccion.class).getResultList();
    }

    // Métodos para Provincia
    public void saveProvincia(Provincia provincia) {
        entityManager.getTransaction().begin();
        entityManager.persist(provincia);
        entityManager.getTransaction().commit();
    }

    public void updateProvincia(Provincia provincia) {
        entityManager.getTransaction().begin();
        entityManager.merge(provincia);
        entityManager.getTransaction().commit();
    }

    public void deleteProvincia(Provincia provincia) {
        entityManager.getTransaction().begin();
        entityManager.remove(provincia);
        entityManager.getTransaction().commit();
    }

    public Provincia findProvincia(long id)
    {
        return entityManager.find(Provincia.class, id);
    }

    public List<Provincia> findAllProvincias() {
        return entityManager.createQuery("SELECT p FROM Provincia p", Provincia.class)
                .getResultList();
    }

    // Métodos para Localidad
    public void saveLocalidad(Localidad localidad) {
        entityManager.getTransaction().begin();
        entityManager.persist(localidad);
        entityManager.getTransaction().commit();
    }

    public void updateLocalidad(Localidad localidad) {
        entityManager.getTransaction().begin();
        entityManager.merge(localidad);
        entityManager.getTransaction().commit();
    }

    public void deleteLocalidad(Localidad localidad) {
        entityManager.getTransaction().begin();
        entityManager.remove(localidad);
        entityManager.getTransaction().commit();
    }

    public List<Localidad> findAllLocalidades() {
        return entityManager.createQuery("SELECT l FROM Localidad l", Localidad.class).getResultList();
    }
    public Localidad findLocalidad(long id) {
        return entityManager.find(Localidad.class, id);
    }

    // Métodos para Municipio
    public void saveMunicipio(Municipio municipio) {
        entityManager.getTransaction().begin();
        entityManager.persist(municipio);
        entityManager.getTransaction().commit();
    }
    public Municipio findMunicipio(long id) {
        return entityManager.find(Municipio.class, id);
    }
    public void updateMunicipio(Municipio municipio) {
        entityManager.getTransaction().begin();
        entityManager.merge(municipio);
        entityManager.getTransaction().commit();
    }

    public void deleteMunicipio(Municipio municipio) {
        entityManager.getTransaction().begin();
        entityManager.remove(municipio);
        entityManager.getTransaction().commit();
    }

    public List<Municipio> findAllMunicipios() {
        return entityManager.createQuery("SELECT m FROM Municipio m", Municipio.class).getResultList();
    }
}
