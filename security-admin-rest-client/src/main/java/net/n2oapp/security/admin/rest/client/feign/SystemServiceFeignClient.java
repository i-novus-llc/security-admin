package net.n2oapp.security.admin.rest.client.feign;

import net.n2oapp.security.admin.rest.api.SystemRestService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "SystemServiceFeignClient", url = "${access.service.api-url}")
public interface SystemServiceFeignClient extends SystemRestService {

}
