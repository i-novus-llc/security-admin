package net.n2oapp.security.admin.rest.client.feign;

import net.n2oapp.security.admin.rest.api.UserRestService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "UserServiceFeignClient", url = "${access.service.api-url}")
public interface UserServiceFeignClient extends UserRestService {

}
