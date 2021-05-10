package eu.tsalliance.auth.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
public class AuthEntryPoint implements AuthenticationEntryPoint, AccessDeniedHandler {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        // TODO: Send error
        log.error("authentication error");
    }

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        eu.tsalliance.auth.exception.account.AccessDeniedException exception = new eu.tsalliance.auth.exception.account.AccessDeniedException();

        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.setStatus(exception.getHttpStatus());
        httpServletResponse.getOutputStream().write(new ObjectMapper().writeValueAsBytes(exception.getResponse()));
    }
}
