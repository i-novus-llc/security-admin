package net.n2oapp.security.admin.auth.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import ru.i_novus.ms.audit.client.autoconfigure.AuditClientAutoConfiguration;
import ru.i_novus.ms.audit.client.autoconfigure.AuditSimpleClientAutoConfiguration;
import ru.inovus.ms.rdm.sync.RdmClientSyncAutoConfiguration;

@SpringBootApplication(exclude = {
        AuditClientAutoConfiguration.class,
        LiquibaseAutoConfiguration.class,
        AuditSimpleClientAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        RdmClientSyncAutoConfiguration.class})
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
