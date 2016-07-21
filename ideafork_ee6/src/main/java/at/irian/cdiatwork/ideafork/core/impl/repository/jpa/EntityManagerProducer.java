package at.irian.cdiatwork.ideafork.core.impl.repository.jpa;

import org.apache.deltaspike.jpa.api.transaction.TransactionScoped;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

@ApplicationScoped
public class EntityManagerProducer {
    @PersistenceUnit(unitName = "ideaForkPU")
    private EntityManagerFactory entityManagerFactory;

    @Produces
    @Default //needed for weld in wildfly8 - see WELD-1620
    @TransactionScoped
    protected EntityManager exposeEntityManagerProxy() {
        return entityManagerFactory.createEntityManager();
    }

    protected void onTransactionEnd(@Disposes @Default /*needed for weld in wildfly8 - see WELD-1620*/ EntityManager entityManager) {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
