package net.n2oapp.security.admin.rdm.feign;

import org.springframework.cloud.openfeign.FeignClient;
import ru.i_novus.ms.rdm.api.rest.DraftRestService;

@FeignClient(name = "RdmDraftRestFeignClient", url = "${rdm.backend.path}")
public interface RdmDraftRestFeignClient extends DraftRestService {

}
