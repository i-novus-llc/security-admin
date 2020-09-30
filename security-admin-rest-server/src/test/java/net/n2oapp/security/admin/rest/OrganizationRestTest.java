package net.n2oapp.security.admin.rest;

import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.model.OrgCategory;
import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.rest.api.OrganizationRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestOrgCategoryCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestOrganizationCriteria;
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

/**
 * Тест Rest сервиса управления организациями
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=8290")
@TestPropertySource("classpath:test.properties")
public class OrganizationRestTest {

    @Autowired
    @Qualifier("organizationRestServiceJaxRsProxyClient")
    private OrganizationRestService client;

    @Test
    public void testFindAll() {
        RestOrganizationCriteria criteria = new RestOrganizationCriteria();
        List<Organization> result = client.getAll(criteria).getContent();
        assertThat(result.size(), is(2));

        criteria.setInn("789");
        criteria.setName("testOrganizationShortName1");
        criteria.setOgrn("123");
        result = client.getAll(criteria).getContent();
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getCode(), is("testOrganization1"));
    }

    @Test
    public void testAllCategories() {
        RestOrgCategoryCriteria criteria = new RestOrgCategoryCriteria();
        List<OrgCategory> orgTypes = client.getAllCategories(criteria).getContent();

        assertThat(orgTypes.size(), is(2));
        criteria.setName("testCategoryName2");

        orgTypes = client.getAllCategories(criteria).getContent();
        assertThat(orgTypes.size(), is(1));
        assertThat(orgTypes.get(0).getId(), is(2));
        assertThat(orgTypes.get(0).getCode(), is("testCategory2"));
        assertThat(orgTypes.get(0).getName(), is("testCategoryName2"));
        assertThat(orgTypes.get(0).getDescription(), is("testCategoryDescription2"));
    }

    @Test
    public void testFindById() {
        Organization organization = client.get(1);

        assertThat(organization.getId(), is(1));
        assertThat(organization.getCode(), is("testOrganization1"));
        assertThat(organization.getInn(), is("789"));
        assertThat(organization.getOgrn(), is("123"));
        assertThat(organization.getEmail(), is("testOrganizationEmail1"));
        assertThat(organization.getFullName(), is("testOrganizationFullName1"));
        assertThat(organization.getKpp(), is("012"));
        assertThat(organization.getOkpo(), is("456"));
        assertThat(organization.getRoles().size(), is(1));
        assertThat(organization.getRoles().get(0).getName(), is("testOrgRole"));
        assertThat(organization.getRoles().get(0).getCode(), is("testOrgRole"));
        assertThat(organization.getOrgCategories().get(0).getCode(), is("testCategory2"));
        assertThat(organization.getOrgCategories().get(0).getName(), is("testCategoryName2"));
        assertThat(organization.getOrgCategories().get(0).getDescription(), is("testCategoryDescription2"));
    }
}
