package eu.tsalliance.auth.model.mail;

import java.util.HashMap;
import java.util.Map;

public class RecoveryMailModel extends MailModel {

    private final String username;
    private final String recoveryToken;

    public RecoveryMailModel(String baseUrl, String recipient, String username, String recoveryToken) {
        super(baseUrl, recipient, "Kontowiederherstellung", "recovery.template.html");
        this.username = username;
        this.recoveryToken = recoveryToken;
    }

    @Override
    public Map<String, Object> getThymeleafVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("username", this.username);
        variables.put("recoveryUrl", this.getBaseUrl() + "auth/recovery?token=" + this.recoveryToken);
        return variables;
    }
}
