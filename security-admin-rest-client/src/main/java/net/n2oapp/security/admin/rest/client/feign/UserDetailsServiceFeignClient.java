package net.n2oapp.security.admin.rest.client.feign;

import net.n2oapp.security.admin.rest.api.UserDetailsRestService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "UserDetailsServiceFeignClient", url = "${access.service.api-url}")
public interface UserDetailsServiceFeignClient extends UserDetailsRestService {

}
