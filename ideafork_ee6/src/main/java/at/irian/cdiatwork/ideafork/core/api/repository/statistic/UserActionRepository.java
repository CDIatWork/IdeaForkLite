package at.irian.cdiatwork.ideafork.core.api.repository.statistic;

import at.irian.cdiatwork.ideafork.core.api.domain.role.User;
import at.irian.cdiatwork.ideafork.core.api.domain.statistic.UserAction;
import at.irian.cdiatwork.ideafork.core.api.monitoring.Monitored;
import at.irian.cdiatwork.ideafork.core.api.repository.change.EntityProcessor;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Collections;
import java.util.List;

@Monitored
@EntityProcessor

@Transactional
@Repository
public abstract class UserActionRepository implements EntityRepository<UserAction, String> {
    @Inject
    private EntityManager entityManager;

    public List<UserAction> loadLatestActivities(User user) {
        try {
            return entityManager.createQuery("select ua from UserAction ua where ua.user = :user order by ua.createdAt desc", UserAction.class)
                    .setParameter("user", user)
                    .setMaxResults(10) //we be done via config like maxNumberOfHighestRatedCategories
                    .getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }
}
