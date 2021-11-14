package demo3;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class ApplicationConfiguration {
    @Bean
    public City city1() {
        return new City("Bucharest");
    }

    @Bean
    public City city2() {
        return new City("Paris");
    }
}
