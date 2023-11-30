package kz.project.reportsservice.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

import java.time.ZoneId;
import java.util.TimeZone;

@Configuration
public class AppConfig {

    @Bean
    public CorsConfiguration corsConfiguration() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        return corsConfiguration;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Almaty")));
        return jacksonObjectMapperBuilder ->
                jacksonObjectMapperBuilder.timeZone(TimeZone.getDefault());
    }
}
