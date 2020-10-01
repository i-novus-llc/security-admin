package net.n2oapp.security.admin.rest;

import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.model.OrgCategory;
import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.rest.api.OrganizationPersistRestService;
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

import java.util.Collections;
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

    @Autowired
    @Qualifier("organizationPersistRestServiceJaxRsProxyClient")
    private OrganizationPersistRestService persistClient;

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

    @Test
    public void testCrudOrganization() {
        Organization organization = new Organization();
        organization.setCode("testOrganizationCreateCode");
        organization.setEmail("testOrganizationCreateEmail");
        organization.setInn("123");
        organization.setOgrn("456");
        organization.setKpp("789");
        organization.setOkpo("012");
        organization.setFullName("testOrganizationCreateFullName");
        organization.setShortName("testOrganizationCreateShortName");
        organization.setRoleIds(Collections.singletonList(1));

        Organization result = persistClient.create(organization);

        assertThat(result.getCode(), is("testOrganizationCreateCode"));
        assertThat(result.getEmail(), is("testOrganizationCreateEmail"));
        assertThat(result.getInn(), is("123"));
        assertThat(result.getOgrn(), is("456"));
        assertThat(result.getKpp(), is("789"));
        assertThat(result.getOkpo(), is("012"));
        assertThat(result.getFullName(), is("testOrganizationCreateFullName"));
        assertThat(result.getShortName(), is("testOrganizationCreateShortName"));
        assertThat(result.getRoles().size(), is(1));
        assertThat(result.getRoles().get(0).getId(), is(1));

        result.setCode("testOrganizationCreateCode_Updt");
        result.setEmail("testOrganizationCreateEmail_Updt");
        result.setInn("123_Updt");
        result.setOgrn("456_Updt");
        result.setKpp("789_Updt");
        result.setOkpo("012_Updt");
        result.setFullName("testOrganizationCreateFullName_Updt");
        result.setShortName("testOrganizationCreateShortName_Updt");
        result.setRoleIds(Collections.singletonList(2));

        result = persistClient.update(result);

        assertThat(result.getCode(), is("testOrganizationCreateCode_Updt"));
        assertThat(result.getEmail(), is("testOrganizationCreateEmail_Updt"));
        assertThat(result.getInn(), is("123_Updt"));
        assertThat(result.getOgrn(), is("456_Updt"));
        assertThat(result.getKpp(), is("789_Updt"));
        assertThat(result.getOkpo(), is("012_Updt"));
        assertThat(result.getFullName(), is("testOrganizationCreateFullName_Updt"));
        assertThat(result.getShortName(), is("testOrganizationCreateShortName_Updt"));
        assertThat(result.getRoles().size(), is(1));
        assertThat(result.getRoles().get(0).getId(), is(2));

        RestOrganizationCriteria criteria = new RestOrganizationCriteria();
        criteria.setName("testOrganizationCreateFullName_Updt");

        assertThat(client.getAll(criteria).getContent().size(), is(1));

        persistClient.delete(result.getId());

        assertThat(client.getAll(criteria).getContent().size(), is(0));
    }
}
