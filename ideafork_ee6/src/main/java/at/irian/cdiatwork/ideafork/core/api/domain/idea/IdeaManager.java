package at.irian.cdiatwork.ideafork.core.api.domain.idea;

import at.irian.cdiatwork.ideafork.core.api.monitoring.Monitored;
import at.irian.cdiatwork.ideafork.core.api.data.view.CategoryView;
import at.irian.cdiatwork.ideafork.core.api.domain.role.User;
import at.irian.cdiatwork.ideafork.core.api.repository.idea.IdeaRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
@Typed(IdeaManager.class)
public class IdeaManager {
    private IdeaValidator ideaValidator;
    private IdeaRepository ideaRepository;

    protected IdeaManager() {
        //needed by proxy-libs
    }

    @Inject
    protected IdeaManager(IdeaValidator ideaValidator, IdeaRepository ideaRepository) {
        this.ideaValidator = ideaValidator;
        this.ideaRepository = ideaRepository;
    }

    @Monitored(maxThreshold = 10)
    public Idea createIdeaFor(String topic, String category, User author) {
        Idea result = new Idea(topic, category, author);

        if (!this.ideaValidator.checkIdea(result)) {
             throw new IllegalArgumentException("Please try it harder next time!");
        }

        return result;
    }

    public Idea importIdea(User user, String ideaString) {
        if (ideaString == null) {
            throw new IllegalArgumentException("No idea to import");
        }

        String[] ideaToImport = ideaString.split(";");

        if (ideaToImport.length >= 2) {
            Idea newIdea = new Idea(ideaToImport[0], ideaToImport[1], user);

            if (ideaToImport.length == 3) {
                newIdea.setDescription(ideaToImport[2]);
            }
            save(newIdea);
            return newIdea;
        }
        throw new IllegalArgumentException("invalid idea to import: " + ideaString);
    }


    @Monitored(maxThreshold = 10)
    public Idea forkIdea(Idea originalIdea, User author) {
        Idea result = new Idea(originalIdea, author);
        return result;
    }

    public void save(Idea entity) {
        ideaRepository.save(entity);
    }

    public void remove(Idea entity) {
        ideaRepository.attachAndRemove(entity);
    }

    public Idea loadById(String id) {
        return ideaRepository.findBy(id);
    }

    public List<Idea> loadAllOfAuthor(User author) {
        return ideaRepository.loadAllOfAuthor(author);
    }

    public List<Idea> search(String searchText) {
        return ideaRepository.search(searchText);
    }

    public List<CategoryView> getHighestRatedCategories() {
        return ideaRepository.getHighestRatedCategories();
    }
}
