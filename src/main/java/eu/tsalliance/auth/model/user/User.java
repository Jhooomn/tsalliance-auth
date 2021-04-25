package eu.tsalliance.auth.model.user;

import eu.tsalliance.auth.model.Application;
import eu.tsalliance.auth.model.Role;
import eu.tsalliance.auth.model.links.LinkedAccount;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ts_users")
public class User extends Profile {

    @Id
    private String id = UUID.randomUUID().toString();
    private String password;
    private String email;

    @ManyToMany
    private List<Application> availableApps = new ArrayList<>();

    @OneToMany
    private List<LinkedAccount> linkedAccounts = new ArrayList<>();

    @ManyToOne
    private Role role;

    public List<LinkedAccount> getLinkedAccounts() {
        return linkedAccounts;
    }

    public void setLinkedAccounts(List<LinkedAccount> linkedAccounts) {
        this.linkedAccounts = linkedAccounts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Application> getAvailableApps() {
        return availableApps;
    }

    public void setAvailableApps(List<Application> availableApps) {
        this.availableApps = availableApps;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
