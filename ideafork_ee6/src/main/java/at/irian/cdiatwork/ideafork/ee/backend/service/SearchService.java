package at.irian.cdiatwork.ideafork.ee.backend.service;

import at.irian.cdiatwork.ideafork.core.api.domain.idea.Idea;

import javax.inject.Inject;
import java.util.List;

@Service
public class SearchService {
    @Inject
    private IdeaService ideaService;

    public List<Idea> searchIdea(String searchText) {
        return ideaService.search(searchText);
    }
}
