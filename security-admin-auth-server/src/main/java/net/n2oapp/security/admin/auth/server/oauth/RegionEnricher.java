package net.n2oapp.security.admin.auth.server.oauth;

import net.n2oapp.security.admin.api.model.Region;
import net.n2oapp.security.admin.api.oauth.UserInfoEnricher;
import net.n2oapp.security.admin.impl.entity.RegionEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class RegionEnricher implements UserInfoEnricher<UserEntity> {
    @Override
    public Object buildValue(UserEntity source) {
        //        todo SECURITY-396
        RegionEntity regionEntity = null;
//        regionEntity = source.getRegion();
        if (isNull(regionEntity)) return null;

        Region region = new Region();
        region.setId(regionEntity.getId());
        region.setCode(regionEntity.getCode());
        region.setName(regionEntity.getName());
        region.setOkato(regionEntity.getOkato());

        return region;
    }

    @Override
    public String getAlias() {
        return "region";
    }
}
