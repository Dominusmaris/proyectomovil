package com.duoc.finanzas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
@Profile("prod")
public class DatabaseUrlConfig {

    @Bean
    public DataSource dataSource(@Value("${DATABASE_URL}") String databaseUrl) {

        if (databaseUrl.startsWith("postgres://")) {
            databaseUrl = databaseUrl.replace("postgres://", "postgresql://");
        }

        URI dbUri = URI.create(databaseUrl);

        String host = dbUri.getHost();
        int port = dbUri.getPort() != -1 ? dbUri.getPort() : 5432;
        String database = dbUri.getPath().substring(1);
        String[] userInfo = dbUri.getUserInfo().split(":");
        String username = userInfo[0];
        String password = userInfo[1];

        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);

        return DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}