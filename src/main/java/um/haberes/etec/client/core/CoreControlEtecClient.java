package um.haberes.etec.client.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import um.haberes.etec.client.core.dto.ControlEtecDto;

@FeignClient(name = "haberes-core-service", contextId = "coreControlEtec")
public interface CoreControlEtecClient {

    /**
     * Devuelve el control ETEC del período o un cuerpo vacío si el período todavía no lo tiene.
     * Se usa {@link ResponseEntity} porque el endpoint responde {@code 200} sin cuerpo cuando
     * el período no está configurado.
     */
    @GetMapping("/api/haberes/core/etec/control/{anho}/{mes}/opcional")
    ResponseEntity<ControlEtecDto> findByPeriodoOpcional(@PathVariable Integer anho, @PathVariable Integer mes);
}
