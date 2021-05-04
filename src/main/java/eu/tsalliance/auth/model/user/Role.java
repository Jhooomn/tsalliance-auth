package eu.tsalliance.auth.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.tsalliance.auth.converter.StringSetConverter;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "ts_roles")
public class Role {

    @Id
    private String id = UUID.randomUUID().toString();

    @Column(unique = true, length = 32, nullable = false)
    private String name;

    private int hierarchy = 0;

    @Column(nullable = false, columnDefinition = "text")
    @Convert(converter = StringSetConverter.class)
    private Set<String> permissions;

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String roleName) {
        this.name = roleName;
    }

    public int getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(int hierarchy) {
        this.hierarchy = hierarchy;
    }

    @Transient
    @JsonIgnore
    public Role censored() {
        Role role = this;
        role.setPermissions(null);
        role.setHierarchy(0);

        return role;
    }
}
