package at.irian.cdiatwork.ideafork.core.api.repository.role;

import at.irian.cdiatwork.ideafork.core.api.domain.role.User;
import at.irian.cdiatwork.ideafork.core.api.monitoring.Monitored;
import at.irian.cdiatwork.ideafork.core.api.repository.change.EntityProcessor;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@Monitored
@EntityProcessor

@Transactional
@Repository
public abstract class UserRepository implements EntityRepository<User, String> {
    @Inject
    private EntityManager entityManager;

    public User loadByNickName(String nickName) {
        try {
            return entityManager.createQuery("select u from User u where u.nickName = :nickName", User.class)
                    .setParameter("nickName", nickName).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public User loadByEmail(String email) {
        try {
            return entityManager.createQuery("select u from User u where u.email = :email", User.class)
                    .setParameter("email", email).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
