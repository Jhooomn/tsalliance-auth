package eu.tsalliance.auth.model.user;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ts_roles")
public class Role {

    @Id
    private String id = UUID.randomUUID().toString();

    @Column(unique = true, length = 32, nullable = false)
    private String roleName;

    @ElementCollection
    @JoinTable(name = "ts_roles_permissions")
    private List<String> permissions = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
