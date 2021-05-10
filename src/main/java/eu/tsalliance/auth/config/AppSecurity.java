package eu.tsalliance.auth.config;

import eu.tsalliance.auth.auth.AuthEntryPoint;
import eu.tsalliance.auth.auth.TokenAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class AppSecurity extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable()
                .exceptionHandling().accessDeniedHandler(new AuthEntryPoint())
                .and()
                .authorizeRequests().antMatchers("**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().disable()
                .logout().disable();

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    public TokenAuthenticationFilter authenticationJwtTokenFilter() {
        return new TokenAuthenticationFilter();
    }

}

