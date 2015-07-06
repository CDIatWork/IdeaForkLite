package at.irian.cdiatwork.ideafork.ee.frontend.jsf.view.controller.user;

import at.irian.cdiatwork.ideafork.ee.backend.service.UserService;
import at.irian.cdiatwork.ideafork.core.api.domain.role.User;
import at.irian.cdiatwork.ideafork.ee.frontend.jsf.view.config.Pages;
import at.irian.cdiatwork.ideafork.ee.frontend.jsf.view.controller.ViewController;
import org.apache.deltaspike.core.api.config.view.ViewConfig;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@ViewController
public class RegistrationViewCtrl {
    @Inject
    private UserService userService;

    private User newUser = new User();

    public Class<? extends ViewConfig> register() {
        User registeredUser = userService.registerUser(this.newUser);

        final String message;
        final Class<? extends ViewConfig> targetPage;
        FacesMessage.Severity severity = FacesMessage.SEVERITY_INFO;
        if (registeredUser != null) {
            message = "Registration successful!";
            targetPage = Pages.User.Login.class;
        } else {
            message = "Registration failed!";
            severity = FacesMessage.SEVERITY_ERROR;
            targetPage = null;
        }

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, message, message));
        return targetPage;
    }

    public User getNewUser() {
        return newUser;
    }
}
