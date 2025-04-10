package com.users.app.configuration;

import com.users.app.entity.AuditTrails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationBean {

    @Bean
    public AuditTrails auditTrails(){
        return new AuditTrails();
    }
}
