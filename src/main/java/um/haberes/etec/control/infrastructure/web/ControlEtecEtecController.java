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

    @GetMapping("/{anho}/{mes}/opcional")
    public ResponseEntity<ControlEtecDto> findByPeriodoOpcional(
            @PathVariable Integer anho, @PathVariable Integer mes) {
        return service.findByPeriodoOpcional(anho, mes);
    }
}
