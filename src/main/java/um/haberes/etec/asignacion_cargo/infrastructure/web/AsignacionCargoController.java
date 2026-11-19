package um.haberes.etec.asignacion_cargo.infrastructure.web;

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
import um.haberes.etec.asignacion_cargo.application.AsignacionCargoService;
import um.haberes.etec.asignacion_cargo.infrastructure.web.dto.AsignacionCargoRequest;
import um.haberes.etec.client.core.dto.EtecAsignacionCargoDto;

@RestController
@RequestMapping("/api/haberes/etec/asignacion-cargo")
@RequiredArgsConstructor
public class AsignacionCargoController {

    private final AsignacionCargoService service;

    @GetMapping("/legajo/{legajoId}")
    public ResponseEntity<List<EtecAsignacionCargoDto>> findAllByLegajo(@PathVariable Long legajoId) {
        return ResponseEntity.ok(service.findAllByLegajo(legajoId));
    }

    @GetMapping("/periodo/{anho}/{mes}")
    public ResponseEntity<List<EtecAsignacionCargoDto>> findAllVigentesByPeriodo(@PathVariable Integer anho,
            @PathVariable Integer mes) {
        return ResponseEntity.ok(service.findAllVigentesByPeriodo(anho, mes));
    }

    @GetMapping("/legajo/{legajoId}/periodo/{anho}/{mes}")
    public ResponseEntity<List<EtecAsignacionCargoDto>> findAllVigentesByLegajoAndPeriodo(
            @PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes) {
        return ResponseEntity.ok(service.findAllVigentesByLegajoAndPeriodo(legajoId, anho, mes));
    }

    @GetMapping("/{asignacionCargoId}")
    public ResponseEntity<EtecAsignacionCargoDto> findById(@PathVariable Long asignacionCargoId) {
        return ResponseEntity.ok(service.findById(asignacionCargoId));
    }

    @PostMapping("/")
    public ResponseEntity<EtecAsignacionCargoDto> create(@Valid @RequestBody AsignacionCargoRequest request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.OK);
    }

    @PutMapping("/{asignacionCargoId}")
    public ResponseEntity<EtecAsignacionCargoDto> update(@PathVariable Long asignacionCargoId,
            @Valid @RequestBody AsignacionCargoRequest request) {
        return ResponseEntity.ok(service.update(asignacionCargoId, request));
    }

    @DeleteMapping("/{asignacionCargoId}")
    public ResponseEntity<Void> delete(@PathVariable Long asignacionCargoId) {
        service.delete(asignacionCargoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
