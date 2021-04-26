package eu.tsalliance.auth.model.links;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ts_linked_discords")
public class LinkedDiscord extends LinkedAccount {

    private String discordId;
    private String accessToken;
    private String refreshToken;

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
