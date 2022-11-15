package net.pagier.mealster.config;

import com.fasterxml.jackson.databind.Module;
import net.pagier.mealster.shared.jackson.PageJacksonModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Module pageJacksonModule() {
        return new PageJacksonModule();
    }
}
