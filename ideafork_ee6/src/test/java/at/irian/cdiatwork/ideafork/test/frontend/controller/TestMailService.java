package at.irian.cdiatwork.ideafork.test.frontend.controller;

import at.irian.cdiatwork.ideafork.core.api.domain.role.User;
import at.irian.cdiatwork.ideafork.core.impl.external.mail.MailService;

import javax.enterprise.inject.Specializes;
import java.util.concurrent.atomic.AtomicInteger;

@Specializes
public class TestMailService extends MailService {
    private AtomicInteger sentWelcomeMessageCount = new AtomicInteger();

    @Override
    public void sendWelcomeMessage(User user) {
        sentWelcomeMessageCount.incrementAndGet();
    }

    public Integer getSentWelcomeMessageCount() {
        int result = sentWelcomeMessageCount.get();
        if (result == 0) {
            return null;
        }
        return result;
    }
}
