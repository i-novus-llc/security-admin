package net.n2oapp.security.admin.rdm.feign;

import org.springframework.cloud.openfeign.FeignClient;
import ru.i_novus.ms.rdm.api.rest.VersionRestService;

@FeignClient(name = "RdmVersionRestFeignClient", url = "${rdm.backend.path}")
public interface RdmVersionRestFeignClient extends VersionRestService {

}
