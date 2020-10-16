package net.n2oapp.security.admin.rest;

import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.model.Region;
import net.n2oapp.security.admin.rest.api.RegionRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestRegionCriteria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Тест Rest сервиса управления регионами
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=8290")
@TestPropertySource("classpath:test.properties")
public class RegionRestTest {

    @Autowired
    @Qualifier("regionRestServiceJaxRsProxyClient")
    private RegionRestService client;

    @Test
    public void testFindAll() {
        RestRegionCriteria criteria = new RestRegionCriteria();
        List<Region> result = client.getAll(criteria).getContent();
        assertThat(result.size(), is(2));

        criteria.setName("testRegion2");

        result = client.getAll(criteria).getContent();

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getName(), is("testRegion2"));
        assertThat(result.get(0).getCode(), is("testRegion2"));
        assertThat(result.get(0).getOkato(), nullValue());

    }

}
