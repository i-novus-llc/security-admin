package net.n2oapp.security.admin.rest.client.feign;

import net.n2oapp.security.admin.rest.api.OrganizationPersistRestService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;

@ConditionalOnProperty(name = "access.organization-persist-mode", havingValue = "rest")
@FeignClient(name = "OrganizationServicePersistFeignClient", url = "${access.service.api-url}")
public interface OrganizationServicePersistFeignClient extends OrganizationPersistRestService {

}
