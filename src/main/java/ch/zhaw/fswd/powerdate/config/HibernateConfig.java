package ch.zhaw.fswd.powerdate.config;

import ch.zhaw.fswd.powerdate.namingstrategy.CustomPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {
    /**
     * Custom hibernate physical naming strategy
     * @return the physical naming strategy
     */
    @Bean
    public CustomPhysicalNamingStrategy physicalNamingStrategy() {
        return new CustomPhysicalNamingStrategy();
    }
}
