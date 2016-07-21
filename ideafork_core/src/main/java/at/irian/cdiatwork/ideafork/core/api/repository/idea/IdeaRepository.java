package at.irian.cdiatwork.ideafork.core.api.repository.idea;

import at.irian.cdiatwork.ideafork.core.api.data.view.CategoryView;
import at.irian.cdiatwork.ideafork.core.api.domain.idea.Idea;
import at.irian.cdiatwork.ideafork.core.api.domain.role.User;
import at.irian.cdiatwork.ideafork.core.api.monitoring.Monitored;
import at.irian.cdiatwork.ideafork.core.api.repository.change.EntityProcessor;
import at.irian.cdiatwork.ideafork.core.impl.config.ApplicationConfig;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

@Monitored
@EntityProcessor

@Transactional
@Repository
public abstract class IdeaRepository implements EntityRepository<Idea, String> {
    @Inject
    private EntityManager entityManager;

    @Inject
    private ApplicationConfig applicationConfig;

    public List<Idea> loadAllOfAuthor(User author) {
        return entityManager.createQuery("select i from Idea i where i.author =:author", Idea.class)
                .setParameter("author", author)
                .getResultList();
    }

    public List<CategoryView> getHighestRatedCategories() {
        return entityManager.createQuery("select new at.irian.cdiatwork.ideafork.core.api.data.view.CategoryView(i.category, count(i.category)) from Idea i group by i.category order by count(i.category) desc")
                .setMaxResults(applicationConfig.maxNumberOfHighestRatedCategories())
                .getResultList();
    }

    public List<Idea> search(String searchText) {
        //instead of using a framework like lucene
        return entityManager.createQuery("select i from Idea i where i.topic like :searchText or i.category like :searchText", Idea.class)
                .setParameter("searchText", "%" + searchText + "%")
                .getResultList();
    }
}
