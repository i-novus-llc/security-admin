package net.n2oapp.security.admin.loader;

import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.model.Region;
import net.n2oapp.security.admin.impl.entity.RegionEntity;
import net.n2oapp.security.admin.impl.repository.RegionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
@EnableEmbeddedPg
class RegionServerLoaderTest {
    @LocalServerPort
    private String port;

    @Autowired
    private RegionRepository repository;

    private URI uri;

    @BeforeEach
    public void before() {
        uri = URI.create("http://localhost:" + port + "/api/loaders/security-admin/regions");
    }

    @Test
    void testLoadNewRegions() {

        Region region1 = buildRegionModel(1, "01", "Республика Татарстан", "92000000000");
        Region region2 = buildRegionModel(2, "002", "Республика Тыва", "93000000000");

        List<Region> data = new ArrayList<>();
        data.add(region1);
        data.add(region2);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<Region>> httpEntity = new HttpEntity<>(data, headers);
        restTemplate.postForLocation(uri, httpEntity);

        List<RegionEntity> allRegions = repository.findAll();

        assertEquals(2, allRegions.size());
        assertTrue(allRegions.stream().allMatch(r -> "01".equals(r.getCode()) || "002".equals(r.getCode())));
        assertTrue(allRegions.stream().allMatch(r -> "Республика Татарстан".equals(r.getName()) || "Республика Тыва".equals(r.getName())));
        assertTrue(allRegions.stream().allMatch(r -> "92000000000".equals(r.getOkato()) || "93000000000".equals(r.getOkato())));
        assertTrue(allRegions.stream().allMatch(r -> r.getIsDeleted() == null));
    }

    @Test
    void testLoadRegionWithoutRequiredFields() {
        Region region1 = buildRegionModel(3, null, null, null);

        List<Region> data = new ArrayList<>();
        data.add(region1);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<Region>> httpEntity = new HttpEntity<>(data, headers);

        try {
            restTemplate.postForLocation(uri, httpEntity);
            fail();
        } catch (HttpClientErrorException.BadRequest badRequest) {
            assertTrue(badRequest.getMessage().contains("Отсутствуют обязательные поля"));
        }


        Region region2 = buildRegionModel(3, "003", null, null);

        List<Region> data2 = new ArrayList<>();
        data2.add(region2);

        httpEntity = new HttpEntity<>(data2, headers);

        try {
            restTemplate.postForLocation(uri, httpEntity);
            fail();
        } catch (HttpClientErrorException.BadRequest badRequest) {
            assertTrue(badRequest.getMessage().contains("Отсутствуют обязательные поля"));
        }
    }

    private Region buildRegionModel(Integer id, String code, String name, String okato) {
        Region region = new Region();
        region.setId(id);
        region.setCode(code);
        region.setName(name);
        region.setOkato(okato);
        return region;
    }
}