package at.irian.cdiatwork.ideafork.core.impl.event;

import akka.actor.UntypedActor;
import at.irian.cdiatwork.ideafork.core.api.domain.role.UserRegisteredEvent;
import at.irian.cdiatwork.ideafork.core.impl.external.mail.MailService;

import javax.inject.Inject;

public class UserRegisteredEventActor extends UntypedActor {
    private final MailService mailService;

    @Inject
    public UserRegisteredEventActor(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof UserRegisteredEvent) {
            this.mailService.sendWelcomeMessage(((UserRegisteredEvent)message).getEntity());
        } else {
            unhandled(message);
        }
    }
}
