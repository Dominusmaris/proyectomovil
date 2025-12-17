package com.duoc.finanzas.config;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RenderDatabaseConfig implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        String databaseUrl = environment.getProperty("DATABASE_URL");

        if (databaseUrl != null && databaseUrl.startsWith("postgresql://")) {
            String jdbcUrl = databaseUrl.replace("postgresql://", "jdbc:postgresql://");

            Map<String, Object> map = new HashMap<>();
            map.put("spring.datasource.url", jdbcUrl);

            environment.getPropertySources().addFirst(new MapPropertySource("renderDatabaseFix", map));
        }
    }
}