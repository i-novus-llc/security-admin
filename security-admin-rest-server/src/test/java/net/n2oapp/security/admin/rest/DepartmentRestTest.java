package net.n2oapp.security.admin.rest;

import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.model.Department;
import net.n2oapp.security.admin.rest.api.DepartmentRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestDepartmentCriteria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Тест REST сервиса управления департаментами
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=8290")
@TestPropertySource("classpath:test.properties")
public class DepartmentRestTest {
    @Autowired
    @Qualifier("departmentRestServiceJaxRsProxyClient")
    private DepartmentRestService client;

    @Test
    public void testFindAll() {
        RestDepartmentCriteria criteria = new RestDepartmentCriteria();
        Page<Department> result = client.findAll(criteria);
        assertThat(result.getTotalElements(), is(2L));

        criteria.setName("testDepartment2");
        result = client.findAll(criteria);
        assertThat(result.getTotalElements(), is(1L));
        assertThat(result.getContent().get(0).getCode(), is("testDepartment2"));
        assertThat(result.getContent().get(0).getName(), is("testDepartment2"));

        criteria.setName("nonExistingDepartment");
        result = client.findAll(criteria);
        assertThat(result.getTotalElements(), is(0L));
    }
}
