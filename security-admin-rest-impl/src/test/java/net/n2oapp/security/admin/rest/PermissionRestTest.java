package net.n2oapp.security.admin.rest;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.rest.api.PermissionRestService;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

;


/**
 * Тест Rest сервиса управления правами доступа
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=8290")
@TestPropertySource("classpath:test.properties")
@AutoConfigureTestDatabase
public class PermissionRestTest {

    @Autowired
    @Qualifier("permissionRestProxyClient")
    private PermissionRestService client;

    @Test
    public void getAll() throws Exception {
        Page<Permission> permissions = client.getAll(1, 4);
        assertEquals(1, permissions.getTotalElements());
    }

}
