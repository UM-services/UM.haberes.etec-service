package um.haberes.etec.control.infrastructure.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import um.haberes.etec.client.core.dto.ControlEtecDto;
import um.haberes.etec.control.application.ControlEtecEtecService;

@RestController
@RequestMapping("/api/haberes/etec/control")
@RequiredArgsConstructor
public class ControlEtecEtecController {

    private final ControlEtecEtecService service;

    /**
     * Control ETEC del período o {@code 200} con cuerpo vacío si todavía no está cargado.
     *
     * <p>Se arma un {@code ResponseEntity} nuevo: devolver el de Feign copia los headers
     * hop-by-hop del upstream y el gateway falla con {@code 500}.</p>
     */
    @GetMapping("/{anho}/{mes}/opcional")
    public ResponseEntity<ControlEtecDto> findByPeriodoOpcional(
            @PathVariable Integer anho, @PathVariable Integer mes) {
        return ResponseEntity.ok(service.findByPeriodoOpcional(anho, mes));
    }
}
