package io.hightide.db;

import io.hightide.db.annotations.Database;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * @author <a href="mailto:gpan@groundzerolabs.com">George Panagiotopoulos</a>
 */
public class PersistenceSupport<E> {

    private EntityManager em;

    private Class<E> entityClass;

    private final EntityType<E> entityType;

    public PersistenceSupport(Class<E> entityClass) {
        this(null, entityClass);
    }

    public PersistenceSupport(String databaseName, Class<E> entityClass) {
        if (isNull(databaseName)) {
            Optional<Database> dbNameAnnot =
                    Optional.ofNullable(entityClass.getAnnotation(Database.class));
            databaseName = dbNameAnnot
                    .flatMap(d -> Optional.ofNullable(d.value()))
                    .orElse("default");
        }
        this.entityClass = entityClass;
        this.em = entityManager(databaseName);
        try {
            this.entityType = em.getMetamodel().entity(this.entityClass);
        } catch (Exception e) {
            throw new EntityNotFoundException("Entity " + this.entityClass.getName() + " not found on database " + databaseName + ".");
        }
    }

    public static <E> PersistenceSupport<E> $(Class<E> entityClass) {
        return new PersistenceSupport<>(entityClass);
    }

    public static <E> PersistenceSupport<E> $(String databaseName, Class<E> entityClass) {
        return new PersistenceSupport<>(databaseName, entityClass);
    }

    public static <E> EntityPersistenceMethods<E> $(E entity) {
        return new EntityPersistenceMethods<>(entity, entityManager("default"));
    }

    public static EntityManager entityManager(String emName) {
        return Persistence.createEntityManagerFactory(emName).createEntityManager();
    }

    public List<E> all() {
        return em.createQuery("SELECT e FROM " + entityClass.getName() + " e", entityClass)
                .getResultList();
    }

    public <K> E get(K id) {
        return em.find(entityClass, id);
    }

//    <T extends ActiveRecord<T>> List<T> where();

    public E save(E entity) {
        em.getTransaction().begin();
        E merge = em.merge(entity);
        em.getTransaction().commit();
        return merge;
    }

    public <K> void delete(K id) {
        String idName = entityType.getId(entityType.getIdType().getJavaType()).getName();
        entityType.getName();

        em.getTransaction().begin();
        em.createQuery("DELETE FROM " + entityType.getName() + " e WHERE e." + idName + " = :id")
                .setParameter("id", id)
                .executeUpdate();
        em.getTransaction().commit();
    }

    static class EntityPersistenceMethods<E> {

        private final E entity;

        private final EntityManager em;

        private final EntityType<E> entityType;

        public EntityPersistenceMethods(E entity, EntityManager em) {
            this.entity = entity;
            this.em = em;
            this.entityType = em.getMetamodel().entity((Class<E>) entity.getClass());
        }

        public E save() {
            em.getTransaction().begin();
            E merge = em.merge(entity);
            em.getTransaction().commit();
            return merge;
        }

        public void delete() {
            Object id = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
            String idName = entityType.getId(entityType.getIdType().getJavaType()).getName();
            em.getTransaction().begin();
            em.createQuery("DELETE FROM " + entityType.getName() + " e WHERE e." + idName + " = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            em.getTransaction().commit();
        }
    }

}
