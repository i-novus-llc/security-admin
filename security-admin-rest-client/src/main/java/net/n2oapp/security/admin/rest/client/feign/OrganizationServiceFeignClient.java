package net.n2oapp.security.admin.rest.client.feign;

import net.n2oapp.security.admin.rest.api.OrganizationRestService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "OrganizationServiceFeignClient", url = "${access.service.url}")
public interface OrganizationServiceFeignClient extends OrganizationRestService {

}
