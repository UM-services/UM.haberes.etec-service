package um.haberes.etec.client.core;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import um.haberes.etec.client.core.dto.EtecPresentismoHorasDto;
import um.haberes.etec.client.core.dto.EtecPresentismoHorasRequestDto;
import um.haberes.etec.client.core.dto.EtecPresentismoHorasUpdateRequestDto;

@FeignClient(name = "haberes-core-service", contextId = "coreEtecPresentismoHoras")
public interface CoreEtecPresentismoHorasClient {

    @GetMapping("/api/haberes/core/etec/presentismo-horas/legajo/{legajoId}/periodo/{anho}/{mes}")
    EtecPresentismoHorasDto findByLegajoAndPeriodo(@PathVariable Long legajoId,
            @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/api/haberes/core/etec/presentismo-horas/periodo/{anho}/{mes}")
    List<EtecPresentismoHorasDto> findByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/api/haberes/core/etec/presentismo-horas/{presentismoHorasId}")
    EtecPresentismoHorasDto findById(@PathVariable Long presentismoHorasId);

    @PostMapping("/api/haberes/core/etec/presentismo-horas/")
    EtecPresentismoHorasDto add(@RequestBody EtecPresentismoHorasRequestDto request);

    @PutMapping("/api/haberes/core/etec/presentismo-horas/{presentismoHorasId}")
    EtecPresentismoHorasDto update(@RequestBody EtecPresentismoHorasUpdateRequestDto request,
            @PathVariable Long presentismoHorasId);

    @DeleteMapping("/api/haberes/core/etec/presentismo-horas/{presentismoHorasId}")
    void delete(@PathVariable Long presentismoHorasId);
}
