package net.n2oapp.security.admin.rest.client.feign;

import net.n2oapp.security.admin.rest.api.DepartmentRestService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "DepartmentServiceFeignClient", url = "${access.service.api-url}")
public interface DepartmentServiceFeignClient extends DepartmentRestService {

}
