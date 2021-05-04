package eu.tsalliance.auth.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements UserDetails {

    @Id
    private String id = UUID.randomUUID().toString();

    @OneToOne
    private Image avatar;

    @OneToMany
    private List<LinkedAccount> linkedAccounts = new ArrayList<>();

    @Column(nullable = false, unique = true, length = 32)
    private String username;
    private String discriminator = RandomUtil.generateRandomNumberString(4);
    private Date createdAt = new Date();

    @Column(nullable = false)
    private String etag = RandomUtil.generateRandomString(16);

    private String password;
    private String email;

    @ManyToMany
    private List<Application> accessableApps = new ArrayList<>();

    @ManyToOne
    private Role role;

    @OneToOne(cascade = CascadeType.DETACH)
    private RecoveryToken recoveryToken;

    public RecoveryToken getRecoveryToken() {
        return recoveryToken;
    }
    public void setRecoveryToken(RecoveryToken recoveryToken) {
        this.recoveryToken = recoveryToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getEtag() {
        return etag;
    }
    public void setEtag(String etag) {
        this.etag = etag;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> permissions = new ArrayList<>();

        if(this.role != null) {
            for(String permission : this.role.getPermissions()) {
                permissions.add(new SimpleGrantedAuthority(permission));
            }
        }

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

    @Transient
    @JsonIgnore
    public Profile getProfile() {
        return new Profile(this.id, this.username, this.discriminator, this.createdAt, this.avatar, this.linkedAccounts);
    }

    @Transient
    @JsonIgnore
    public User censored() {
        User user = this;
        user.setRecoveryToken(null);
        user.setPassword(null);

        if(user.getRole() != null) {
            user.setRole(user.getRole().censored());
        }

        return user;
    }
}
