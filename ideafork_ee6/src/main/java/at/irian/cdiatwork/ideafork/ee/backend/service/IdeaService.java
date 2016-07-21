package at.irian.cdiatwork.ideafork.ee.backend.service;

import at.irian.cdiatwork.ideafork.core.api.data.view.CategoryView;
import at.irian.cdiatwork.ideafork.core.api.domain.idea.Idea;
import at.irian.cdiatwork.ideafork.core.api.domain.idea.IdeaValidator;
import at.irian.cdiatwork.ideafork.core.api.domain.promotion.PromotionRequest;
import at.irian.cdiatwork.ideafork.core.api.domain.role.User;
import at.irian.cdiatwork.ideafork.core.api.monitoring.Monitored;
import at.irian.cdiatwork.ideafork.core.api.repository.idea.IdeaRepository;
import at.irian.cdiatwork.ideafork.core.api.repository.promotion.PromotionRepository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class IdeaService {
    @Inject
    private IdeaRepository ideaRepository;

    @Inject
    private IdeaValidator ideaValidator;

    @Inject
    private PromotionRepository promotionRepository;

    @Monitored(maxThreshold = 10)
    public Idea createIdeaFor(String topic, String category, User author) {
        Idea result = new Idea(topic, category, author);

        if (!this.ideaValidator.checkIdea(result)) {
            throw new IllegalArgumentException("Please try it harder next time!");
        }

        return result;
    }

    public Idea save(Idea idea) {
        return ideaRepository.save(idea);
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
            ideaRepository.save(newIdea);
            return newIdea;
        }
        throw new IllegalArgumentException("invalid idea to import: " + ideaString);
    }

    public Idea forkIdea(Idea originalIdea, User author) {
        Idea result = new Idea(originalIdea, author);
        return result;
    }

    public Idea loadById(String baseIdeaId) {
        return ideaRepository.findBy(baseIdeaId);
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

    public void remove(Idea idea) {
        this.ideaRepository.attachAndRemove(idea);
    }

    public void requestIdeaPromotion(PromotionRequest promotionRequest) {
        this.promotionRepository.save(promotionRequest);
    }

    @Monitored(maxThreshold = 10)
    public List<Idea> loadRecentlyPromotedIdeas(User user) {
        List<Idea> originalIdeaList = this.promotionRepository.loadRecentlyPromotedIdeas(user);
        List<Idea> result = new ArrayList<Idea>(originalIdeaList.size());
        for (Idea idea : originalIdeaList) {
            result.add(forkIdea(idea, user));
        }
        return result;
    }

    public List<PromotionRequest> loadRecentIdeaPromotions(User currentUser, String searchHint) {
        if (searchHint == null) {
            searchHint = "*";
        }
        return this.promotionRepository.loadRecentIdeaPromotions(currentUser, searchHint);
    }

    public void promoteIdea(PromotionRequest selectedPromotionRequest) {
        this.promotionRepository.promoteIdea(selectedPromotionRequest);
    }
}