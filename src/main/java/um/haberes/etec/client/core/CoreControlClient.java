package um.haberes.etec.client.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import um.haberes.etec.client.core.dto.ControlDto;

@FeignClient(name = "haberes-core-service", contextId = "coreControl")
public interface CoreControlClient {

    @GetMapping("/api/haberes/core/control/periodo/{anho}/{mes}")
    ControlDto findByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);
}
