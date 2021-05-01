package eu.tsalliance.auth.model.user;

import eu.tsalliance.auth.model.Application;
import eu.tsalliance.auth.model.image.Image;
import eu.tsalliance.auth.model.links.LinkedAccount;
import eu.tsalliance.auth.utils.RandomUtil;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "ts_users")
public class User implements UserDetails {

    @Id
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false, unique = true, length = 32)
    private String username;

    @Column(nullable = false)
    private String etag = RandomUtil.generateRandomString(16);

    private Date createdAt = new Date();

    private String password;
    private String email;

    @OneToOne
    private Image avatar;

    private int discriminator = RandomUtil.generateRandomNumber(4);

    @ManyToMany
    private List<Application> accessableApps = new ArrayList<>();

    @OneToMany
    private List<LinkedAccount> linkedAccounts = new ArrayList<>();

    @ManyToOne
    private Role role;

    @OneToOne
    private RecoveryToken recoveryToken;

    public Image getAvatar() {
        return avatar;
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }

    public int getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(int discriminator) {
        this.discriminator = discriminator;
    }

    public RecoveryToken getRecoveryToken() {
        return recoveryToken;
    }

    public void setRecoveryToken(RecoveryToken recoveryToken) {
        this.recoveryToken = recoveryToken;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO: Role permissions as authority

        List<GrantedAuthority> permissions = new ArrayList<>();
        permissions.add(new SimpleGrantedAuthority("permission"));
        return permissions;
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

    public List<Application> getAccessableApps() {
        return accessableApps;
    }

    public void setAccessableApps(List<Application> accessableApps) {
        this.accessableApps = accessableApps;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
