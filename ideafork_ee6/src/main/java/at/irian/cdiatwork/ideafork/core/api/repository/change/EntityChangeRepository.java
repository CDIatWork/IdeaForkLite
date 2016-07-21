package at.irian.cdiatwork.ideafork.core.api.repository.change;

import at.irian.cdiatwork.ideafork.core.api.domain.change.EntityChange;
import at.irian.cdiatwork.ideafork.core.api.monitoring.Monitored;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@Monitored
@EntityProcessor

@Repository
public abstract class EntityChangeRepository implements EntityRepository<EntityChange, String> {
    @Inject
    private EntityManager entityManager;

    public EntityChange findRevision(String entityId, long entityVersionToFind) {
        try {
            return entityManager.createQuery(
                    "select ec from EntityChange ec where ec.entityId =:id and ec.entityVersion =:version", EntityChange.class)
                    .setParameter("id", entityId)
                    .setParameter("version", entityVersionToFind)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
