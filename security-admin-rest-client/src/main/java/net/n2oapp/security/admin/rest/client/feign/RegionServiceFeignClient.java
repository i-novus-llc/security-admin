package net.n2oapp.security.admin.rest.client.feign;

import net.n2oapp.security.admin.rest.api.RegionRestService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "RegionServiceFeignClient", url = "${access.service.api-url}")
public interface RegionServiceFeignClient extends RegionRestService {

}
