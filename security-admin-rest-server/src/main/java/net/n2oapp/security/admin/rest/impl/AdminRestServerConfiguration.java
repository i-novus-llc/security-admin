package net.n2oapp.security.admin.rest.impl;

import org.apache.cxf.bus.spring.SpringBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"net.n2oapp.security.admin.rest.impl", "net.n2oapp.security.admin.api"})
public class AdminRestServerConfiguration {

    @Bean
    public SpringBus cxf() {
        SpringBus springBus = new SpringBus();
        springBus.setProperty("default.wae.mapper.least.specific", false);
        return springBus;
    }
}
