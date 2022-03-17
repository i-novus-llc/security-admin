package net.n2oapp.security.admin.impl.loader;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.platform.loader.server.ServerLoader;
import net.n2oapp.platform.loader.server.ServerLoaderSettings;
import net.n2oapp.security.admin.api.model.Region;
import net.n2oapp.security.admin.impl.entity.RegionEntity;
import net.n2oapp.security.admin.impl.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class RegionServerLoader extends ServerLoaderSettings<Region> implements ServerLoader<Region> {

    private static final String WRONG_REQUEST = "exception.wrongRequest";
    private static final String MISSING_REQUIRED_FIELDS = "exception.missingRequiredFields";

    private final RegionRepository regionRepository;

    @Autowired
    public RegionServerLoader(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @Override
    @Transactional
    public void load(List<Region> data, String subject) {
        if (isCreateRequired()) {
            for (Region region : data) {
                create(region);
            }
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

    private void create(Region region) {
        if (region == null) throw new UserException(WRONG_REQUEST);
        if (region.getCode() == null || region.getName() == null)
            throw new UserException(MISSING_REQUIRED_FIELDS);
        regionRepository.save(map(region));
    }

    private RegionEntity map(Region model) {
        RegionEntity entity = new RegionEntity();
        entity.setCode(model.getCode());
        entity.setName(model.getName());
        entity.setOkato(model.getOkato());
        return entity;
    }
}
