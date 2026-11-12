package um.haberes.etec.client.core;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import um.haberes.etec.client.core.dto.EtecAsignacionCargoDto;
import um.haberes.etec.client.core.dto.EtecAsignacionCargoRequestDto;

@FeignClient(name = "haberes-core-service", contextId = "coreEtecAsignacionCargo")
public interface CoreEtecAsignacionCargoClient {

    @GetMapping("/api/haberes/core/etec/asignacion-cargo/legajo/{legajoId}")
    List<EtecAsignacionCargoDto> findByLegajo(@PathVariable Long legajoId);

    @GetMapping("/api/haberes/core/etec/asignacion-cargo/periodo/{anho}/{mes}")
    List<EtecAsignacionCargoDto> findVigentesByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/api/haberes/core/etec/asignacion-cargo/legajo/{legajoId}/periodo/{anho}/{mes}")
    List<EtecAsignacionCargoDto> findVigentesByLegajoAndPeriodo(@PathVariable Long legajoId,
            @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/api/haberes/core/etec/asignacion-cargo/categorias")
    List<Integer> findCategoriasAsignadas();

    @GetMapping("/api/haberes/core/etec/asignacion-cargo/categoria/{categoriaId}/exists")
    Boolean existsByCategoria(@PathVariable Integer categoriaId);

    @GetMapping("/api/haberes/core/etec/asignacion-cargo/{asignacionCargoId}")
    EtecAsignacionCargoDto findById(@PathVariable Long asignacionCargoId);

    @PostMapping("/api/haberes/core/etec/asignacion-cargo/")
    EtecAsignacionCargoDto add(@RequestBody EtecAsignacionCargoRequestDto request);

    @PutMapping("/api/haberes/core/etec/asignacion-cargo/{asignacionCargoId}")
    EtecAsignacionCargoDto update(@RequestBody EtecAsignacionCargoRequestDto request,
            @PathVariable Long asignacionCargoId);

    @DeleteMapping("/api/haberes/core/etec/asignacion-cargo/{asignacionCargoId}")
    void delete(@PathVariable Long asignacionCargoId);
}
