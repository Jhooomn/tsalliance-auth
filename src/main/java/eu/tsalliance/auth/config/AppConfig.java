package eu.tsalliance.auth.config;

import eu.tsalliance.validator.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public Validator validator() {
        return new Validator();
    }

}
