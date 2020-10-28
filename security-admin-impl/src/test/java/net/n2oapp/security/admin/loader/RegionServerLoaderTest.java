package net.n2oapp.security.admin.loader;

import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import net.n2oapp.security.admin.api.model.Region;
import net.n2oapp.security.admin.impl.entity.RegionEntity;
import net.n2oapp.security.admin.impl.loader.RegionServerLoader;
import net.n2oapp.security.admin.impl.repository.RegionRepository;
import net.n2oapp.security.admin.loader.builder.RegionBuilder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
@EnableEmbeddedPg
class RegionServerLoaderTest {

    @Autowired
    private RegionServerLoader serverLoader;
    @Autowired
    private RegionRepository repository;

    @Test
    void testLoadWrongModel() {
        Region nullRegion = RegionBuilder.buildNullModel();

        List<Region> data = new ArrayList<>();
        data.add(nullRegion);

        Throwable thrown = catchThrowable(() -> serverLoader.load(data,"security-admin"));
        assertEquals("exception.wrongRequest", thrown.getMessage());
    }

    @Test
    void testLoadNewRegions() {
        repository.deleteAll();

        Region region1 = RegionBuilder.buildRegionModel(1,"01", "Республика Татарстан", "92000000000");
        Region region2 = RegionBuilder.buildRegionModel(2,"002", "Республика Тыва", "93000000000");

        List<Region> data = new ArrayList<>();
        data.add(region1);
        data.add(region2);

        serverLoader.load(data, "security-admin");

        List<RegionEntity> allRegions = repository.findAll();

        assertEquals(2, allRegions.size());
        assertTrue(allRegions.stream().allMatch(r -> "01".equals(r.getCode()) || "002".equals(r.getCode())));
        assertTrue(allRegions.stream().allMatch(r -> "Республика Татарстан".equals(r.getName()) || "Республика Тыва".equals(r.getName())));
        assertTrue(allRegions.stream().allMatch(r -> "92000000000".equals(r.getOkato()) || "93000000000".equals(r.getOkato())));
        assertTrue(allRegions.stream().allMatch(r -> Boolean.FALSE == r.getIsDeleted()));
    }

    @Test
    void testLoadRegionWithoutRequiredFields() {
        Region region1 = RegionBuilder.buildRegionModel(3, null, null, null);

        List<Region> data = new ArrayList<>();
        data.add(region1);

        Throwable thrown = catchThrowable(() -> serverLoader.load(data,"security-admin"));
        assertEquals("exception.missingRequiredFields", thrown.getMessage());

        Region region2 = RegionBuilder.buildRegionModel(3, "003", null, null);

        List<Region> data2 = new ArrayList<>();
        data2.add(region2);

        thrown = catchThrowable(() -> serverLoader.load(data2,"security-admin"));
        assertEquals("exception.missingRequiredFields", thrown.getMessage());
    }

    @Test
    void testUpdateExistingRegions() {
        repository.deleteAll();

        Region region1 = RegionBuilder.buildRegionModel(1,"01", "Республика Татарстан", "92000000000");
        Region region2 = RegionBuilder.buildRegionModel(2,"002", "Республика Тыва", "93000000000");

        List<Region> data = new ArrayList<>();
        data.add(region1);
        data.add(region2);

        serverLoader.load(data, "security-admin");

        List<RegionEntity> allRegions = repository.findAll();

        assertEquals(2, allRegions.size());
        assertTrue(allRegions.stream().allMatch(r -> "01".equals(r.getCode()) || "002".equals(r.getCode())));
        assertTrue(allRegions.stream().allMatch(r -> "Республика Татарстан".equals(r.getName()) || "Республика Тыва".equals(r.getName())));
        assertTrue(allRegions.stream().allMatch(r -> "92000000000".equals(r.getOkato()) || "93000000000".equals(r.getOkato())));
        assertTrue(allRegions.stream().allMatch(r -> Boolean.FALSE == r.getIsDeleted()));

        Region region3 = RegionBuilder.buildRegionModel(1,"03", "Краснодарский край", "01000000000");
        Region region4 = RegionBuilder.buildRegionModel(2,"04", "Красноярский край", "03000000000");

        List<Region> data2 = new ArrayList<>();
        data2.add(region3);
        data2.add(region4);

        serverLoader.load(data2, "security-admin");

        allRegions = repository.findAll();

        assertEquals(2, allRegions.size());
        assertTrue(allRegions.stream().allMatch(r -> "03".equals(r.getCode()) || "04".equals(r.getCode())));
        assertTrue(allRegions.stream().allMatch(r -> "Краснодарский край".equals(r.getName()) || "Красноярский край".equals(r.getName())));
        assertTrue(allRegions.stream().allMatch(r -> "01000000000".equals(r.getOkato()) || "03000000000".equals(r.getOkato())));
        assertTrue(allRegions.stream().allMatch(r -> Boolean.FALSE == r.getIsDeleted()));
    }
}