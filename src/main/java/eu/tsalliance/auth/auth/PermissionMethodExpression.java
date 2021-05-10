package eu.tsalliance.auth.auth;

import eu.tsalliance.auth.model.user.Role;
import eu.tsalliance.auth.model.user.User;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PermissionMethodExpression extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private final Authentication authentication;

    private Object filterObject;
    private Object returnObject;
    private Object target;

    public PermissionMethodExpression(Authentication authentication) {
        super(authentication);
        this.authentication = authentication;
    }

    public boolean hasPermission(String permission) {
        Collection<? extends GrantedAuthority> authorities = Optional.ofNullable(this.authentication.getAuthorities()).orElse(new ArrayList<>());
        List<String> permissions = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        return permissions.contains("*") || permissions.contains(permission);
    }

    public boolean canModifyUser(User user) {
        Optional<Role> requesterRole = Optional.ofNullable(((User) this.authentication.getPrincipal()).getRole());
        Optional<Role> userRole = Optional.ofNullable(user.getRole());

        return requesterRole.orElse(new Role()).getHierarchy() > userRole.orElse(new Role()).getHierarchy();
    }

    @Override
    public void setFilterObject(Object o) {
        this.filterObject = o;
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public void setReturnObject(Object o) {
        this.returnObject = o;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public Object getThis() {
        return target;
    }
}
