package eu.tsalliance.auth.auth;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

public class PermissionExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        PermissionMethodExpression expression = new PermissionMethodExpression(authentication);
        expression.setTrustResolver(new AuthenticationTrustResolverImpl());
        expression.setRoleHierarchy(getRoleHierarchy());

        return expression;
    }
}
