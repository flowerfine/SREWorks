package com.alibaba.tesla.authproxy.config.jpa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.GregorianCalendar;
import java.util.TimeZone;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "utcDateTimeProvider")
public class JpaConfig {

    @Bean
    public DateTimeProvider utcDateTimeProvider() {
        return () -> new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    }
}
