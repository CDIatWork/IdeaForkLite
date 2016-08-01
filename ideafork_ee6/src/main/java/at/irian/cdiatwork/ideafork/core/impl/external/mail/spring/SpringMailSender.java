package at.irian.cdiatwork.ideafork.core.impl.external.mail.spring;

import org.apache.deltaspike.core.api.exclude.Exclude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/*
    usually there would be an own jar which contains spring-beans.
    to avoid an extra jar (just for illustrating a simple Spring integration) we exclude this class for CDI.
    -> Spring manages it (to support @Autowired) -> SpringBridgeExtension introduces it to CDI
 */
@Exclude //just needed because we are in a bda
public class SpringMailSender {
    @Autowired
    private JavaMailSender mailSender;

    public void send(String senderAddress, String recipientAddress, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientAddress);
        message.setFrom(senderAddress);
        message.setSubject(subject);
        message.setText(text);
        this.mailSender.send(message);
    }
}
