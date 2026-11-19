package um.haberes.etec.client.core;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import um.haberes.etec.client.core.dto.CategoriaDto;
import um.haberes.etec.client.core.dto.CategoriaRequestDto;

@FeignClient(name = "haberes-core-service", contextId = "coreCategoria")
public interface CoreCategoriaClient {

    @GetMapping("/api/haberes/core/categoria/puntos")
    List<CategoriaDto> findConPuntos();

    @GetMapping("/api/haberes/core/categoria/last")
    CategoriaDto findLast();

    @GetMapping("/api/haberes/core/categoria/{categoriaId}")
    CategoriaDto findById(@PathVariable Integer categoriaId);

    @PostMapping("/api/haberes/core/categoria/{anho}/{mes}")
    CategoriaDto add(@RequestBody CategoriaRequestDto request, @PathVariable Integer anho, @PathVariable Integer mes);

    @PutMapping("/api/haberes/core/categoria/{categoriaId}/{anho}/{mes}")
    CategoriaDto update(@RequestBody CategoriaRequestDto request, @PathVariable Integer categoriaId,
            @PathVariable Integer anho, @PathVariable Integer mes);

    @DeleteMapping("/api/haberes/core/categoria/{categoriaId}")
    void delete(@PathVariable Integer categoriaId);
}
