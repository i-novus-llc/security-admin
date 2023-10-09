package net.n2oapp.security.admin.rest.client.feign;

import net.n2oapp.security.admin.rest.api.AccountRestService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "AccountServiceFeignClient", url = "${access.service.api-url}")
public interface AccountServiceFeignClient extends AccountRestService {

}
