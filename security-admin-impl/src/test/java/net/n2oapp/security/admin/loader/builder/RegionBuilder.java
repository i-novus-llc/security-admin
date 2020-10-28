package net.n2oapp.security.admin.loader.builder;

import net.n2oapp.security.admin.api.model.Region;

public class RegionBuilder {
    private RegionBuilder() {}

    public static Region buildNullModel() {
        return null;
    }

    public static Region buildRegionModel(Integer id, String code, String name, String okato) {
        Region region = new Region();
        region.setId(id);
        region.setCode(code);
        region.setName(name);
        region.setOkato(okato);
        return region;
    }
}
