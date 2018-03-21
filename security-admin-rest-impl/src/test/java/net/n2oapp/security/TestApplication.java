package net.n2oapp.security;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import net.n2oapp.security.admin.rest.api.RestPage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;

import java.text.SimpleDateFormat;

/**
 * Стартовая точка запуска Spring Boot
 */
@SpringBootApplication
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Value("${spring.jackson.date-format}")
    private String dateFormat;

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public JacksonJsonProvider jsonProvider() {
        return new JacksonJsonProvider(cxfObjectMapper());
    }

    private ObjectMapper cxfObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new SimpleDateFormat(dateFormat));
        SimpleModule springDataModule = new SimpleModule("ClientBackendModule", Version.unknownVersion());
        SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
        resolver.addMapping(Page.class, RestPage.class);
        springDataModule.setAbstractTypes(resolver);
        mapper.registerModule(springDataModule);
        return mapper;
    }

}


