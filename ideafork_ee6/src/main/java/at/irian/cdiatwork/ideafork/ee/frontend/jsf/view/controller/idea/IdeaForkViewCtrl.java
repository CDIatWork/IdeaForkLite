package at.irian.cdiatwork.ideafork.ee.frontend.jsf.view.controller.idea;

import at.irian.cdiatwork.ideafork.core.api.domain.idea.Idea;
import at.irian.cdiatwork.ideafork.ee.backend.service.IdeaService;
import at.irian.cdiatwork.ideafork.ee.frontend.jsf.view.config.Pages;
import at.irian.cdiatwork.ideafork.ee.frontend.jsf.view.controller.ViewController;
import at.irian.cdiatwork.ideafork.ee.shared.ActiveUserHolder;

import javax.inject.Inject;
import java.io.Serializable;

@ViewController
public class IdeaForkViewCtrl implements Serializable {
    @Inject
    private IdeaEditViewCtrl ideaEditViewCtrl;

    @Inject
    private IdeaService ideaService;

    @Inject
    private ActiveUserHolder userHolder;

    public Class<? extends Pages.Idea> forkIdea(Idea currentIdea) {
        Idea forkedIdea = ideaService.forkIdea(currentIdea, userHolder.getAuthenticatedUser());
        ideaEditViewCtrl.editIdea(forkedIdea);
        return Pages.Idea.Edit.class;
    }
}
