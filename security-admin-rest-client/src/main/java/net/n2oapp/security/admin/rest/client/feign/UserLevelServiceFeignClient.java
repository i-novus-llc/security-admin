package net.n2oapp.security.admin.rest.client.feign;

import net.n2oapp.security.admin.rest.api.UserLevelRestService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "UserLevelServiceFeignClient", url = "${access.service.api-url}")
public interface UserLevelServiceFeignClient extends UserLevelRestService {

}
