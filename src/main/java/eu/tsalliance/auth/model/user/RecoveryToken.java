package eu.tsalliance.auth.model.user;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "ts_users_recovery")
public class RecoveryToken {

    @Id
    private String id = UUID.randomUUID().toString();

    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    private Date expiresAt = new Date(System.currentTimeMillis() + (1000*60*60*24));

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
}
