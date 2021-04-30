package eu.tsalliance.auth.model.mail;

import java.util.Map;

public abstract class MailModel {

    private final String recipient;
    private final String mailSubject;
    private final String htmlTemplateFile;
    private final String mailIcon = "https://raw.githubusercontent.com/z3ttee/tsassets/main/logo/ts_logo.png";

    public MailModel(String recipient, String mailSubject, String htmlTemplateFile) {
        this.recipient = recipient;
        this.mailSubject = mailSubject;
        this.htmlTemplateFile = htmlTemplateFile;
    }

    public String getRecipient() {
        return recipient;
    }
    public String getMailSubject() {
        return mailSubject;
    }
    public String getHtmlTemplateFile() {
        return htmlTemplateFile;
    }
    public String getMailIcon() {
        return mailIcon;
    }

    public abstract Map<String, Object> getThymeleafVariables();
}
