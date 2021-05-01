package eu.tsalliance.auth.model.mail;

import java.util.HashMap;
import java.util.Map;

public class InviteMailModel extends MailModel {

    private final String inviter;
    private final String inviteUrl;

    public InviteMailModel(String baseUrl, String recipient, String inviter, String inviteUrl) {
        super(baseUrl, recipient, "Du wurdest eingeladen!", "invite.template.html", inviter + " von TSAlliance");
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
