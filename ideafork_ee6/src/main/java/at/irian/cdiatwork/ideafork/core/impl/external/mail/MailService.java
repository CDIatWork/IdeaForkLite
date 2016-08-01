package at.irian.cdiatwork.ideafork.core.impl.external.mail;

import at.irian.cdiatwork.ideafork.core.api.domain.role.User;
import at.irian.cdiatwork.ideafork.core.impl.external.mail.spring.SpringMailSender;
import org.apache.deltaspike.core.api.config.ConfigResolver;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MailService {
    @Inject
    private SpringMailSender mailSender;

    public void sendWelcomeMessage(User user) {
        String senderAddress = ConfigResolver.getProjectStageAwarePropertyValue("ideafork.sender", "admin@ideafork.com");
        String subject = "Welcome " + user.getNickName();
        String text = "Welcome @ IdeaFork!";
        this.mailSender.send(senderAddress, user.getEmail(), subject, text);
    }
}
