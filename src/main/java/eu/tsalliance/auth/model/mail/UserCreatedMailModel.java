package eu.tsalliance.auth.model.mail;

import java.util.HashMap;
import java.util.Map;

public class UserCreatedMailModel extends MailModel {

    private final String username;
    private final String password;

    public UserCreatedMailModel(String recipient, String username, String password) {
        super(recipient, "Willkommen in der Allianz!", "user-created.template.html");
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }

    @Override
    public Map<String, Object> getThymeleafVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("username", this.username);
        variables.put("password", this.password);
        return variables;
    }
}
