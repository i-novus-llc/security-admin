package net.n2oapp.security.admin.service;

import net.n2oapp.security.admin.api.criteria.OrganizationCriteria;
import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.impl.service.OrganizationServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;

/**
 * Тест сервиса управления организациями
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
public class OrganizationServiceImplTest {

    @Autowired
    OrganizationServiceImpl organizationService;

    @Test
    public void createOrg() {

        Organization organizationResponse = prepareOrgForTest("2");
        assertNotNull(organizationResponse.getId());
        assertThat(organizationResponse.getCode(), is("2"));
        assertThat(organizationResponse.getShortName(), is("2"));
        assertThat(organizationResponse.getOgrn(), is("2"));
        assertThat(organizationResponse.getOkpo(), is("2"));
        assertThat(organizationResponse.getFullName(), is("2"));
        assertThat(organizationResponse.getInn(), is("2"));
        assertThat(organizationResponse.getKpp(), is("2"));
        assertThat(organizationResponse.getLegalAddress(), is("2"));
        assertThat(organizationResponse.getEmail(), is("2"));
    }

    @Test
    public void updateOrg() {
        Organization organizationResponse = prepareOrgForTest("3");
        Integer orgId = organizationResponse.getId();
        organizationResponse.setCode("99");
        organizationResponse.setShortName("4");
        organizationResponse.setOgrn("4");
        organizationResponse.setOkpo("4");
        organizationResponse.setFullName("4");
        organizationResponse.setInn("4");
        organizationResponse.setKpp("4");
        organizationResponse.setLegalAddress("4");
        organizationResponse.setEmail("4");
        organizationResponse = organizationService.update(organizationResponse);

        assertThat(organizationResponse.getId(), is(orgId));
        assertThat(organizationResponse.getCode(), is("99"));
        assertThat(organizationResponse.getShortName(), is("4"));
        assertThat(organizationResponse.getOgrn(), is("4"));
        assertThat(organizationResponse.getOkpo(), is("4"));
        assertThat(organizationResponse.getFullName(), is("4"));
        assertThat(organizationResponse.getInn(), is("4"));
        assertThat(organizationResponse.getKpp(), is("4"));
        assertThat(organizationResponse.getLegalAddress(), is("4"));
        assertThat(organizationResponse.getEmail(), is("4"));
    }

    @Test
    public void deleteOrg() {
        Organization organizationResponse = prepareOrgForTest("6");
        organizationService.delete(organizationResponse.getId());
        Throwable thrown = catchThrowable(() -> organizationService.find(organizationResponse.getId()));
        assertThat(thrown.getMessage(), is("exception.OrganizationNotFound"));
    }

    @Test
    public void findOrg() {
        Organization organization = prepareOrgForTest("7");
        Organization response = organizationService.find(organization.getId());
        assertThat(response.getId(), is(organization.getId()));
    }

    @Test
    public void findAll() {
        OrganizationCriteria criteria = new OrganizationCriteria();
        criteria.setInn("test_inn6");
        List<Organization> organizations = organizationService.findAll(criteria).getContent();
        assertThat(organizations.size(), is(1));
        assertThat(organizations.get(0).getCode(), is("test_code6"));

        criteria = new OrganizationCriteria();
        criteria.setOgrn("test_ogrn4");
        organizations = organizationService.findAll(criteria).getContent();
        assertThat(organizations.size(), is(1));
        assertThat(organizations.get(0).getCode(), is("test_code4"));

        criteria = new OrganizationCriteria();
        criteria.setName("test_full_name5");
        organizations = organizationService.findAll(criteria).getContent();
        assertThat(organizations.size(), is(1));
        assertThat(organizations.get(0).getCode(), is("test_code5"));

        criteria = new OrganizationCriteria();
        criteria.setShortName("test_short_name2");
        organizations = organizationService.findAll(criteria).getContent();
        assertThat(organizations.size(), is(1));
        assertThat(organizations.get(0).getCode(), is("test_code2"));
    }

    private Organization prepareOrgForTest(String testValue) {
        Organization organization = new Organization();
        organization.setCode(testValue);
        organization.setShortName(testValue);
        organization.setOgrn(testValue);
        organization.setOkpo(testValue);
        organization.setFullName(testValue);
        organization.setInn(testValue);
        organization.setKpp(testValue);
        organization.setLegalAddress(testValue);
        organization.setEmail(testValue);

        return organizationService.create(organization);
    }
}
