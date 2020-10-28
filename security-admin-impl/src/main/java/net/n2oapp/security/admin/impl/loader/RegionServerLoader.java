package net.n2oapp.security.admin.impl.loader;

import net.n2oapp.platform.loader.server.ServerLoader;
import net.n2oapp.security.admin.api.model.Region;
import net.n2oapp.security.admin.api.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RegionServerLoader implements ServerLoader<Region> {

    private final RegionService regionService;

    @Autowired
    public RegionServerLoader(RegionService regionService) {
        this.regionService = regionService;
    }

    @Override
    public void load(List<Region> data, String subject) {
        for (Region region: data) {
            regionService.create(region);
        }
    }

    @Override
    public String getTarget() {
        return "regions";
    }

    @Override
    public Class<Region> getDataType() {
        return Region.class;
    }
}
