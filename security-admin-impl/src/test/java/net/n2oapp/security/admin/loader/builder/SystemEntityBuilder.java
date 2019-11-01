package net.n2oapp.security.admin.loader.builder;

import net.n2oapp.security.admin.impl.entity.SystemEntity;

public class SystemEntityBuilder {
    public static SystemEntity buildSystemEntity1() {
        SystemEntity systemEntity = new SystemEntity();
        systemEntity.setCode("system1");
        systemEntity.setName("system1");
        return systemEntity;
    }

    public static SystemEntity buildSystemEntity2() {
        SystemEntity systemEntity = new SystemEntity();
        systemEntity.setCode("system2");
        systemEntity.setName("system2");
        return systemEntity;
    }
}
