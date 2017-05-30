package at.irian.cdiatwork.ideafork.test.frontend.controller;

import at.irian.cdiatwork.ideafork.core.api.domain.idea.Idea;
import at.irian.cdiatwork.ideafork.core.api.domain.promotion.PromotionRequest;
import at.irian.cdiatwork.ideafork.core.api.domain.role.User;
import at.irian.cdiatwork.ideafork.core.api.repository.change.EntityChangeRepository;
import at.irian.cdiatwork.ideafork.ee.backend.service.IdeaService;
import at.irian.cdiatwork.ideafork.ee.frontend.jsf.view.config.Pages;
import at.irian.cdiatwork.ideafork.ee.frontend.jsf.view.controller.idea.IdeaCreateViewCtrl;
import at.irian.cdiatwork.ideafork.ee.frontend.jsf.view.controller.navigation.NavigationController;
import at.irian.cdiatwork.ideafork.ee.frontend.jsf.view.controller.promotion.PromotionWizardCtrl;
import at.irian.cdiatwork.ideafork.ee.frontend.jsf.view.controller.user.LoginViewCtrl;
import at.irian.cdiatwork.ideafork.ee.frontend.jsf.view.controller.user.RegistrationViewCtrl;
import at.irian.cdiatwork.ideafork.ee.frontend.jsf.view.model.SelectableEntity;
import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.core.api.config.view.ViewConfig;
import org.apache.deltaspike.core.spi.scope.window.WindowContext;
import org.apache.deltaspike.core.util.ExceptionUtils;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.List;

@RunWith(CdiTestRunner.class)
public class IdeaForkBaseFlowTest {
    @Inject
    private WindowContext windowContext;

    @Inject
    private ContextControl contextControl;

    @Inject
    private RegistrationViewCtrl registrationViewCtrl;

    @Inject
    private LoginViewCtrl loginViewCtrl;

    @Inject
    private IdeaCreateViewCtrl ideaCreateViewCtrl;

    @Inject
    private NavigationController navigationController;

    @Inject
    private PromotionWizardCtrl promotionWizardCtrl;

    @Inject
    private IdeaService ideaService;

    @Inject
    private TestMailService testMailService;

    @Inject
    private EntityChangeRepository entityChangeRepository;

    @Before
    public void initTestWindow() {
        windowContext.activateWindow("testWindow");
    }

    @Test
    public void flowFromRegistrationToIdeaPromotion() {
        registrationViewCtrl.getNewUser().setNickName("os890");
        registrationViewCtrl.getNewUser().setEmail("os890@test.org");
        registrationViewCtrl.getNewUser().setPassword("test");
        Class<? extends ViewConfig> navigationResult = registrationViewCtrl.register();

        Assert.assertEquals(Pages.User.Login.class, navigationResult);
        Assert.assertFalse(FacesContext.getCurrentInstance().getMessageList().isEmpty());

        newRequest();

        Assert.assertTrue(FacesContext.getCurrentInstance().getMessageList().isEmpty());

        loginViewCtrl.setEmail("os890@test.org");
        loginViewCtrl.setPassword("test");
        navigationResult = loginViewCtrl.login();
        Assert.assertEquals(Pages.Idea.Overview.class, navigationResult);

        Assert.assertFalse(FacesContext.getCurrentInstance().getMessageList().isEmpty());

        newRequest();

        final String topic = "Test Page-Beans";
        final String category = "test";

        ideaCreateViewCtrl.setTopic(topic);
        ideaCreateViewCtrl.setCategory(category);
        navigationResult = ideaCreateViewCtrl.save();
        Assert.assertEquals(Pages.Idea.Overview.class, navigationResult);

        newRequest();

        navigationResult = navigationController.toIdeaPromotionWizard();
        Assert.assertEquals(Pages.Promotion.PromotionWizard.Step1.class, navigationResult);

        newRequest(); //simulates a redirect

        promotionWizardCtrl.onPreRenderView();

        List<SelectableEntity<Idea>> selectableIdeas = promotionWizardCtrl.getSelectableIdeaList();
        Assert.assertNotNull(selectableIdeas);
        Assert.assertEquals(1, selectableIdeas.size());

        newRequest();

        promotionWizardCtrl.select(selectableIdeas.iterator().next());

        newRequest();

        navigationResult = promotionWizardCtrl.toStep2();
        Assert.assertEquals(Pages.Promotion.PromotionWizard.Step2.class, navigationResult);

        newRequest(); //simulates a redirect

        promotionWizardCtrl.onPreRenderView();

        newRequest();

        promotionWizardCtrl.getPromotionRequest().setDescription("promote it");
        navigationResult = promotionWizardCtrl.showConfirmation();
        Assert.assertEquals(Pages.Promotion.PromotionWizard.FinalStep.class, navigationResult);

        newRequest(); //simulates a redirect

        promotionWizardCtrl.onPreRenderView();

        newRequest();

        promotionWizardCtrl.savePromotionRequest();

        newRequest();

        User testUser = new User("tester", null, null);
        List<PromotionRequest> foundPromotionRequests = ideaService.loadRecentIdeaPromotions(testUser, "*");
        Assert.assertNotNull(foundPromotionRequests);
        Assert.assertEquals(1, foundPromotionRequests.size());
        PromotionRequest loadedPromotionRequest = foundPromotionRequests.iterator().next();
        Assert.assertEquals("promote it", loadedPromotionRequest.getDescription());
        Assert.assertEquals(topic, loadedPromotionRequest.getIdeaForPromotion().getTopic());
        Assert.assertEquals(category, loadedPromotionRequest.getIdeaForPromotion().getCategory());

        newRequest();

        foundPromotionRequests = ideaService.loadRecentIdeaPromotions(testUser, null);
        Assert.assertNotNull(foundPromotionRequests);
        Assert.assertEquals(1, foundPromotionRequests.size());
        loadedPromotionRequest = foundPromotionRequests.iterator().next();
        Assert.assertEquals("promote it", loadedPromotionRequest.getDescription());
        Assert.assertEquals(topic, loadedPromotionRequest.getIdeaForPromotion().getTopic());
        Assert.assertEquals(category, loadedPromotionRequest.getIdeaForPromotion().getCategory());

        newRequest();

        foundPromotionRequests = ideaService.loadRecentIdeaPromotions(testUser, "Page");
        Assert.assertNotNull(foundPromotionRequests);
        Assert.assertEquals(1, foundPromotionRequests.size());
        loadedPromotionRequest = foundPromotionRequests.iterator().next();
        Assert.assertEquals("promote it", loadedPromotionRequest.getDescription());
        Assert.assertEquals(topic, loadedPromotionRequest.getIdeaForPromotion().getTopic());
        Assert.assertEquals(category, loadedPromotionRequest.getIdeaForPromotion().getCategory());

        newRequest();

        foundPromotionRequests = ideaService.loadRecentIdeaPromotions(testUser, "x");
        Assert.assertNotNull(foundPromotionRequests);
        Assert.assertTrue(foundPromotionRequests.isEmpty());

        int sentWelcomeMessageCount = getSentWelcomeMessageCount();
        Assert.assertEquals(1, sentWelcomeMessageCount);
    }

    private void newRequest() {
        contextControl.stopContext(RequestScoped.class);
        contextControl.startContext(RequestScoped.class);
        initTestWindow();
    }

    private Integer getSentWelcomeMessageCount() {
        return new RetryHelper<Integer>() {
            @Override
            protected Integer execute() {
                Integer result = testMailService.getSentWelcomeMessageCount();

                if (result == null) {
                    return 0;
                }
                return result;
            }
        }.start();
    }

    private abstract class RetryHelper<T> {
        private int tryCount = 0;

        public T start() {
            tryCount++;
            T result = execute();

            if (result != null || tryCount > 10) {
                return result;
            }

            try {
                Thread.sleep(tryCount * 20);
            } catch (InterruptedException e) {
                ExceptionUtils.throwAsRuntimeException(e);
            }

            return start();
        }

        protected abstract T execute();
    }
}
