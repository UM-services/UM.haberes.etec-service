package um.haberes.etec.client.core;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import um.haberes.etec.client.core.dto.EtecPresentismoDto;
import um.haberes.etec.client.core.dto.EtecPresentismoRequestDto;
import um.haberes.etec.client.core.dto.EtecPresentismoUpdateRequestDto;

@FeignClient(name = "haberes-core-service", contextId = "coreEtecPresentismo")
public interface CoreEtecPresentismoClient {

    @GetMapping("/api/haberes/core/etec/presentismo/legajo/{legajoId}/periodo/{anho}/{mes}")
    List<EtecPresentismoDto> findByLegajoAndPeriodo(@PathVariable Long legajoId,
            @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/api/haberes/core/etec/presentismo/periodo/{anho}/{mes}")
    List<EtecPresentismoDto> findByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/api/haberes/core/etec/presentismo/asignacion/{asignacionCargoId}/periodo/{anho}/{mes}")
    EtecPresentismoDto findByAsignacionAndPeriodo(@PathVariable Long asignacionCargoId,
            @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/api/haberes/core/etec/presentismo/{presentismoId}")
    EtecPresentismoDto findById(@PathVariable Long presentismoId);

    @PostMapping("/api/haberes/core/etec/presentismo/")
    EtecPresentismoDto add(@RequestBody EtecPresentismoRequestDto request);

    @PutMapping("/api/haberes/core/etec/presentismo/{presentismoId}")
    EtecPresentismoDto update(@RequestBody EtecPresentismoUpdateRequestDto request,
            @PathVariable Long presentismoId);

    @DeleteMapping("/api/haberes/core/etec/presentismo/{presentismoId}")
    void delete(@PathVariable Long presentismoId);
}
