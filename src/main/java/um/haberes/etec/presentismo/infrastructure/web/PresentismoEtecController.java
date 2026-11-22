package um.haberes.etec.presentismo.infrastructure.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import um.haberes.etec.client.core.dto.EtecPresentismoDto;
import um.haberes.etec.presentismo.application.PresentismoEtecService;
import um.haberes.etec.presentismo.infrastructure.web.dto.PresentismoRequest;
import um.haberes.etec.presentismo.infrastructure.web.dto.PresentismoUpdateRequest;

@RestController
@RequestMapping("/api/haberes/etec/presentismo")
@RequiredArgsConstructor
public class PresentismoEtecController {

    private final PresentismoEtecService service;

    @GetMapping("/legajo/{legajoId}/periodo/{anho}/{mes}")
    public ResponseEntity<List<EtecPresentismoDto>> findAllByLegajoAndPeriodo(
            @PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes) {
        return ResponseEntity.ok(service.findAllByLegajoAndPeriodo(legajoId, anho, mes));
    }

    @GetMapping("/periodo/{anho}/{mes}")
    public ResponseEntity<List<EtecPresentismoDto>> findAllByPeriodo(
            @PathVariable Integer anho, @PathVariable Integer mes) {
        return ResponseEntity.ok(service.findAllByPeriodo(anho, mes));
    }

    @GetMapping("/asignacion/{asignacionCargoId}/periodo/{anho}/{mes}")
    public ResponseEntity<EtecPresentismoDto> findByAsignacionAndPeriodo(
            @PathVariable Long asignacionCargoId, @PathVariable Integer anho, @PathVariable Integer mes) {
        return ResponseEntity.ok(service.findByAsignacionAndPeriodo(asignacionCargoId, anho, mes));
    }

    @GetMapping("/{presentismoId}")
    public ResponseEntity<EtecPresentismoDto> findById(@PathVariable Long presentismoId) {
        return ResponseEntity.ok(service.findById(presentismoId));
    }

    @PostMapping("/")
    public ResponseEntity<EtecPresentismoDto> create(
            @Valid @RequestBody PresentismoRequest request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{presentismoId}")
    public ResponseEntity<EtecPresentismoDto> update(
            @PathVariable Long presentismoId,
            @Valid @RequestBody PresentismoUpdateRequest request) {
        return ResponseEntity.ok(service.update(presentismoId, request));
    }

    @DeleteMapping("/{presentismoId}")
    public ResponseEntity<Void> delete(@PathVariable Long presentismoId) {
        service.delete(presentismoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
