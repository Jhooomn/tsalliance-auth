package eu.tsalliance.auth.model.mail;

import java.util.HashMap;
import java.util.Map;

public class WelcomeMailModel extends MailModel {

    private final String username;

    public WelcomeMailModel(String recipient, String username) {
        super(recipient, "Willkommen in der Allianz!", "welcome.template.html");
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public Map<String, Object> getThymeleafVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("username", this.username);
        return variables;
    }
}
