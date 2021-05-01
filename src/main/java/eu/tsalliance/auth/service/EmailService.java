package eu.tsalliance.auth.service;

import eu.tsalliance.auth.config.AllianceProperties;
import eu.tsalliance.auth.model.mail.MailModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Optional;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private AllianceProperties allianceProperties;

    @Value("${spring.mail.username}")
    private Optional<String> MAIL_SENDER;

    public void sendMail(MailModel mailModel) {
        new Thread(() -> {
            try {
                Context context = new Context();
                context.setVariable("appUrl", allianceProperties.getUrl());
                context.setVariable("mailIcon", mailModel.getMailIcon());
                context.setVariables(mailModel.getThymeleafVariables());

                MimeMessage mimeMailMessage = this.mailSender.createMimeMessage();
                mimeMailMessage.setSubject(mailModel.getMailSubject());
                mimeMailMessage.setFrom(MAIL_SENDER.orElse(""));
                mimeMailMessage.setRecipients(Message.RecipientType.TO, mailModel.getRecipient());

                String htmlContent = this.templateEngine.process(mailModel.getHtmlTemplateFile(), context);
                mimeMailMessage.setContent(htmlContent, "text/html");

                this.mailSender.send(mimeMailMessage);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();

    }

}
