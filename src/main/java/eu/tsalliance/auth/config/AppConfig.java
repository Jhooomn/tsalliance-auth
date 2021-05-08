package eu.tsalliance.auth.config;

import eu.tsalliance.apiutils.validator.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class AppConfig {

    @Bean
    @RequestScope
    public Validator validator() {
        return new Validator();
    }

}
