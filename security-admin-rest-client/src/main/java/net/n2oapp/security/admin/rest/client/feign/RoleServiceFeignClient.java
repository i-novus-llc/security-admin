package net.n2oapp.security.admin.rest.client.feign;

import net.n2oapp.security.admin.rest.api.RoleRestService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "RoleServiceFeignClient", url = "${access.service.url}")
public interface RoleServiceFeignClient extends RoleRestService {

}
