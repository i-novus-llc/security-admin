package net.n2oapp.security.admin.rdm.feign;

import org.springframework.cloud.openfeign.FeignClient;
import ru.i_novus.ms.rdm.api.service.PublishService;

@FeignClient(name = "RdmPublishRestFeignClient", url = "${rdm.backend.path}")
public interface RdmPublishRestFeignClient extends PublishService {

}
