package eu.tsalliance.auth.model;

import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.utils.RandomUtil;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ts_invites")
public class Invite {

    @Id
    private String id = RandomUtil.generateRandomHash(12);

    @ManyToOne
    private User inviter;

    private int uses = 0;
    private int maxUses = Integer.MAX_VALUE;
    private Date createdAt = new Date();
    private boolean canExpire = true;
    private Date expiresAt = new Date(System.currentTimeMillis() + (1000*60*60*24));

    public boolean canExpire() {
        return canExpire;
    }

    public void setCanExpire(boolean canExpire) {
        this.canExpire = canExpire;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getInviter() {
        return inviter;
    }

    public void setInviter(User inviter) {
        this.inviter = inviter;
    }

    public int getUses() {
        return uses;
    }

    public void setUses(int uses) {
        this.uses = uses;
    }

    public int getMaxUses() {
        return maxUses;
    }

    public void setMaxUses(int maxUses) {
        this.maxUses = maxUses;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
}
