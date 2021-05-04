package eu.tsalliance.auth.model.user;

import eu.tsalliance.auth.model.image.Image;
import eu.tsalliance.auth.model.links.LinkedAccount;

import java.util.Date;
import java.util.List;

public class Profile {

    private String id;
    private String username;
    private Date createdAt;
    private Image avatar;
    private String discriminator;
    private List<LinkedAccount> linkedAccounts;

    public Profile(String id, String username, String discriminator, Date createdAt, Image avatar, List<LinkedAccount> linkedAccounts) {
        this.id = id;
        this.username = username;
        this.createdAt = createdAt;
        this.avatar = avatar;
        this.linkedAccounts = linkedAccounts;
        this.discriminator = discriminator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Image getAvatar() {
        return avatar;
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }

    public List<LinkedAccount> getLinkedAccounts() {
        return linkedAccounts;
    }

    public void setLinkedAccounts(List<LinkedAccount> linkedAccounts) {
        this.linkedAccounts = linkedAccounts;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }
}
