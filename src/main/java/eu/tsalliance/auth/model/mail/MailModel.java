package eu.tsalliance.auth.model.mail;

import java.util.Map;

public abstract class MailModel {

    private final String recipient;
    private final String mailSubject;
    private final String htmlTemplateFile;
    private final String baseUrl;
    private final String mailIcon = "https://raw.githubusercontent.com/z3ttee/tsassets/main/logo/ts_logo.png";
    private final String fromName;

    public MailModel(String baseUrl, String recipient, String mailSubject, String htmlTemplateFile) {
        this.baseUrl = baseUrl;
        this.recipient = recipient;
        this.mailSubject = mailSubject;
        this.htmlTemplateFile = htmlTemplateFile;
        this.fromName = null;
    }
    public MailModel(String baseUrl, String recipient, String mailSubject, String htmlTemplateFile, String fromName) {
        this.baseUrl = baseUrl;
        this.recipient = recipient;
        this.mailSubject = mailSubject;
        this.htmlTemplateFile = htmlTemplateFile;
        this.fromName = fromName;
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
    public String getBaseUrl() {
        return baseUrl;
    }
    public String getFromName() {
        return fromName;
    }

    public abstract Map<String, Object> getThymeleafVariables();
}
