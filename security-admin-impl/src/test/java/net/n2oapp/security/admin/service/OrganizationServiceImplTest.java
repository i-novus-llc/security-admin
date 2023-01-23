package net.n2oapp.security.admin.service;

import net.n2oapp.platform.test.autoconfigure.pg.EnableEmbeddedPg;
import net.n2oapp.security.admin.api.criteria.OrgCategoryCriteria;
import net.n2oapp.security.admin.api.criteria.OrganizationCriteria;
import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.impl.service.OrganizationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Тест сервиса управления организациями
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
@EnableEmbeddedPg
public class OrganizationServiceImplTest {

    @Autowired
    OrganizationServiceImpl organizationService;

    @Test
    public void createOrg() {
        String id = "2";
        Organization organizationResponse = organizationService.create(prepareOrgRequest(id));
        assertNotNull(organizationResponse.getId());
        assertEquals(id, organizationResponse.getCode());
        assertEquals(id, organizationResponse.getShortName());
        assertEquals(id, organizationResponse.getOgrn());
        assertEquals(id, organizationResponse.getOkpo());
        assertEquals(id, organizationResponse.getFullName());
        assertEquals(id, organizationResponse.getInn());
        assertEquals(id, organizationResponse.getKpp());
        assertEquals(id, organizationResponse.getLegalAddress());
        assertEquals(id, organizationResponse.getEmail());
        assertEquals(id, organizationResponse.getExtUid());
        assertEquals(2, organizationResponse.getRoles().size());
        assertEquals(100, organizationResponse.getRoles().get(0).getId());
        assertEquals(101, organizationResponse.getRoles().get(1).getId());
    }

    @Test
    public void createOrgException() {
        Organization organizationResponse = organizationService.create(prepareOrgRequest("8"));

        Throwable thrown = catchThrowable(() -> organizationService.create(organizationResponse));
        assertEquals("exception.uniqueOrganization", thrown.getMessage());

        organizationResponse.setId(null);
        organizationResponse.setCode("8");
        thrown = catchThrowable(() -> organizationService.create(organizationResponse));
        assertEquals("exception.uniqueOrganizationCode", thrown.getMessage());

        organizationResponse.setCode("2463242");
        organizationResponse.setOgrn("8");
        thrown = catchThrowable(() -> organizationService.create(organizationResponse));
        assertEquals("exception.uniqueOgrn", thrown.getMessage());
    }

    @Test
    public void updateOrgException() {
        Organization organizationResponse = organizationService.create(prepareOrgRequest("9"));
        Integer orgId = organizationResponse.getId();

        organizationResponse.setId(null);
        Throwable thrown = catchThrowable(() -> organizationService.update(organizationResponse));
        assertEquals("exception.NullOrganizationId", thrown.getMessage());

        organizationResponse.setId(9999999);
        thrown = catchThrowable(() -> organizationService.update(organizationResponse));
        assertEquals("exception.OrganizationNotFound", thrown.getMessage());

        organizationService.create(prepareOrgRequest("10"));

        organizationResponse.setId(orgId);
        organizationResponse.setCode("10");
        thrown = catchThrowable(() -> organizationService.update(organizationResponse));
        assertEquals("exception.uniqueOrganizationCode", thrown.getMessage());
    }

    @Test
    public void updateOrg() {
        Organization organizationResponse = organizationService.create(prepareOrgRequest("3"));
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

        assertEquals(orgId, organizationResponse.getId());
        assertEquals("99", organizationResponse.getCode());
        assertEquals("4", organizationResponse.getShortName());
        assertEquals("4", organizationResponse.getOgrn());
        assertEquals("4", organizationResponse.getOkpo());
        assertEquals("4", organizationResponse.getFullName());
        assertEquals("4", organizationResponse.getInn());
        assertEquals("4", organizationResponse.getKpp());
        assertEquals("4", organizationResponse.getLegalAddress());
        assertEquals("4", organizationResponse.getEmail());
    }

    @Test
    public void deleteOrg() {
        Organization organizationResponse = organizationService.create(prepareOrgRequest("6"));
        organizationService.delete(organizationResponse.getId());
        Throwable thrown = catchThrowable(() -> organizationService.delete(organizationResponse.getId()));
        assertEquals("exception.OrganizationNotFound", thrown.getMessage());
    }

    @Test
    public void findOrg() {
        Organization organization = organizationService.create(prepareOrgRequest("7"));
        Organization response = organizationService.find(organization.getId());
        assertEquals(organization.getId(), response.getId());
    }

    @Test
    public void findAllOrg() {
        OrganizationCriteria criteria = new OrganizationCriteria();
        criteria.setInn("test_inn6");
        List<Organization> organizations = organizationService.findAll(criteria).getContent();
        assertEquals(1, organizations.size());
        assertEquals("test_code6", organizations.get(0).getCode());
        criteria.setOrders(null);
        assertNotNull(organizationService.findAll(criteria).getContent());

        criteria = new OrganizationCriteria();
        criteria.setOgrn("test_ogrn4");
        organizations = organizationService.findAll(criteria).getContent();
        assertEquals(1, organizations.size());
        assertEquals("test_code4", organizations.get(0).getCode());

        criteria = new OrganizationCriteria();
        criteria.setName("test_full_name5");
        organizations = organizationService.findAll(criteria).getContent();
        assertEquals(1, organizations.size());
        assertEquals("test_code5", organizations.get(0).getCode());

        criteria = new OrganizationCriteria();
        criteria.setShortName("test_short_name2");
        organizations = organizationService.findAll(criteria).getContent();
        assertEquals(1, organizations.size());
        assertEquals("test_code2", organizations.get(0).getCode());
    }

    @Test
    public void findAllCategory() {
        assertNotNull(organizationService.findAllCategories(new OrgCategoryCriteria()));
        OrgCategoryCriteria categoryCriteria = new OrgCategoryCriteria();
        categoryCriteria.setOrders(null);
        assertNotNull(organizationService.findAllCategories(categoryCriteria));
    }

    private Organization prepareOrgRequest(String testValue) {
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
        organization.setExtUid(testValue);
        organization.setRoleIds(Arrays.asList(100, 101));
        organization.setOrgCategoryIds(Arrays.asList(1));
        return organization;
    }
}
