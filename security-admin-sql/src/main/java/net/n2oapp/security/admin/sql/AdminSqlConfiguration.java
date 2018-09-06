package net.n2oapp.security.admin.sql;



import net.n2oapp.security.admin.commons.AdminCommonsConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * Конфигурация security-admin-sql
 */
@Configuration
@ComponentScan("net.n2oapp.security.admin.sql")
@Import(AdminCommonsConfiguration.class)
public class AdminSqlConfiguration {

}



