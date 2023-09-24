package net.n2oapp.security.admin.rest.client.feign;

import net.n2oapp.security.admin.rest.api.PermissionRestService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "PermissionServiceFeignClient", url = "${access.service.api-url}")
public interface PermissionServiceFeignClient extends PermissionRestService {

}
