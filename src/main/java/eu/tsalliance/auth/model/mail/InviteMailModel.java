package eu.tsalliance.auth.model.mail;

import java.util.HashMap;
import java.util.Map;

public class InviteMailModel extends MailModel {

    private final String inviter;
    private final String inviteUrl;

    public InviteMailModel(String recipient, String inviter, String inviteUrl) {
        super(recipient, "Du wurdest eingeladen!", "invite.template.html");
        this.inviter = inviter;
        this.inviteUrl = inviteUrl;
    }

    @Override
    public Map<String, Object> getThymeleafVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("inviter", this.inviter);
        variables.put("inviteUrl", this.inviteUrl);
        return variables;
    }
}
