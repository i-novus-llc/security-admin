package net.n2oapp.security.admin.rest.client.feign;

import net.n2oapp.security.admin.rest.api.AccountTypeRestService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "AccountTypeFeignClient", url = "${access.service.api-url}")
public interface AccountTypeFeignClient extends AccountTypeRestService {

}
