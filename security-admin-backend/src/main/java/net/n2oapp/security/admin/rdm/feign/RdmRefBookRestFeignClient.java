package net.n2oapp.security.admin.rdm.feign;

import org.springframework.cloud.openfeign.FeignClient;
import ru.i_novus.ms.rdm.api.service.RefBookService;

@FeignClient(name = "RdmRefBookRestFeignClient", url = "${rdm.backend.path}")
public interface RdmRefBookRestFeignClient extends RefBookService {

}
