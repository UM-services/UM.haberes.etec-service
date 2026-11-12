package um.haberes.etec.client.core;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import um.haberes.etec.client.core.dto.CargoLiquidacionDto;
import um.haberes.etec.client.core.dto.ReemplazoCargosRequestDto;

@FeignClient(name = "haberes-core-service", contextId = "coreCargoLiquidacion")
public interface CoreCargoLiquidacionClient {

    @GetMapping("/api/haberes/core/cargoliquidacion/legajo/{legajoId}/{anho}/{mes}")
    List<CargoLiquidacionDto> findByLegajo(@PathVariable Long legajoId, @PathVariable Integer anho,
            @PathVariable Integer mes);

    @GetMapping("/api/haberes/core/cargoliquidacion/periodo/{anho}/{mes}/todos")
    List<CargoLiquidacionDto> findByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);

    @PostMapping("/api/haberes/core/cargoliquidacion/reemplazar/periodo/{anho}/{mes}")
    List<CargoLiquidacionDto> replaceByPeriodo(@RequestBody ReemplazoCargosRequestDto request,
            @PathVariable Integer anho, @PathVariable Integer mes);

    @PostMapping("/api/haberes/core/cargoliquidacion/reemplazar/legajo/{legajoId}/{anho}/{mes}")
    List<CargoLiquidacionDto> replaceByLegajo(@RequestBody ReemplazoCargosRequestDto request,
            @PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);
}
