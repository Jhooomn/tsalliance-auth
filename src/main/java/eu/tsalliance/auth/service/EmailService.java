package eu.tsalliance.auth.service;

import eu.tsalliance.auth.config.AllianceProperties;
import eu.tsalliance.auth.model.mail.*;
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

    /**
     * Send a mail
     * @param mailModel Data to be in the mail
     */
    public void sendMail(MailModel mailModel) {
        new Thread(() -> {
            try {
                Context context = new Context();
                context.setVariable("appUrl", this.getAllianceBaseUrl());
                context.setVariable("mailIcon", mailModel.getMailIcon());
                context.setVariables(mailModel.getThymeleafVariables());

                MimeMessage mimeMailMessage = this.mailSender.createMimeMessage();
                mimeMailMessage.setSubject(mailModel.getMailSubject());
                mimeMailMessage.setFrom(this.formatSender(mailModel));
                mimeMailMessage.setRecipients(Message.RecipientType.TO, mailModel.getRecipient());

                String htmlContent = this.templateEngine.process(mailModel.getHtmlTemplateFile(), context);
                mimeMailMessage.setContent(htmlContent, "text/html");

                this.mailSender.send(mimeMailMessage);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();

    }

    /**
     * Format the senders mail address accordingly
     * @param mailModel Mail model
     * @return String
     */
    private String formatSender(MailModel mailModel) {
        if(mailModel.getFromName() != null) {
            return mailModel.getFromName() + " <" + MAIL_SENDER.orElse("") + ">";
        }

        return MAIL_SENDER.orElse("");
    }

    /**
     * Send recovery mail to user
     * @param recipient Recipient's email address
     * @param username Recipient's username
     * @param tokenId Recovery token id
     */
    public void sendRecoveryMail(String recipient, String username, String tokenId) {
        RecoveryMailModel mailModel = new RecoveryMailModel(this.getAllianceBaseUrl(), recipient, username, tokenId);
        this.sendMail(mailModel);
    }

    /**
     * Send welcome mail to user
     * @param recipient Recipient's email address
     * @param username Recipient's username
     */
    public void sendWelcomeMail(String recipient, String username) {
        WelcomeMailModel mailModel = new WelcomeMailModel(this.getAllianceBaseUrl(), recipient, username);
        this.sendMail(mailModel);
    }

    /**
     * Send account creation notification mail to user
     * @param recipient Recipient's email address
     * @param username Recipient's username
     * @param rawPassword Password to be sent to user
     */
    public void sendUserCreatedMail(String recipient, String username, String rawPassword) {
        UserCreatedMailModel mailModel = new UserCreatedMailModel(this.getAllianceBaseUrl(), recipient, username, rawPassword);
        this.sendMail(mailModel);
    }

    /**
     * Send invite mail to user
     * @param recipient Recipient's email address
     * @param senderUsername Sender's username
     * @param inviteId Id of the invite
     */
    public void sendInviteMail(String recipient, String senderUsername, String inviteId) {
        InviteMailModel mailModel =  new InviteMailModel(this.getAllianceBaseUrl(), recipient, senderUsername, this.allianceProperties.getBaseUrl() + "auth/register?invite=" + inviteId);
        this.sendMail(mailModel);
    }

    public String getAllianceBaseUrl() {
        return this.allianceProperties.getBaseUrl();
    }
}
