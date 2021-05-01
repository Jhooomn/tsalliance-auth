package eu.tsalliance.auth.auth;

import eu.tsalliance.auth.model.user.User;
import eu.tsalliance.auth.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

/**
 * The responsibility of this class is  extract token from the request headers
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer";

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationParam = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION)).orElse(null);
        String token = Optional.ofNullable(authorizationParam)
                .map(value -> value.replace(BEARER + " ", ""))
                .map(String::trim)
                .orElse(null);

        Optional<User> user = this.authenticationService.findUserDetailsByJwt(token);
        UsernamePasswordAuthenticationToken authentication;

        if(user.isPresent()) {
            authentication = new UsernamePasswordAuthenticationToken(user.get(), null, user.get().getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        } else {
            User dummy = new User();

            authentication = new UsernamePasswordAuthenticationToken(dummy, null, Collections.emptyList());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
