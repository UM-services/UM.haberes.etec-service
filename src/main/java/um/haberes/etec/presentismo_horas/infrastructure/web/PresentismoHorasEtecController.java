package um.haberes.etec.presentismo_horas.infrastructure.web;

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
import um.haberes.etec.client.core.dto.EtecPresentismoHorasDto;
import um.haberes.etec.presentismo_horas.application.PresentismoHorasEtecService;
import um.haberes.etec.presentismo_horas.infrastructure.web.dto.PresentismoHorasRequest;
import um.haberes.etec.presentismo_horas.infrastructure.web.dto.PresentismoHorasUpdateRequest;

@RestController
@RequestMapping("/api/haberes/etec/presentismo-horas")
@RequiredArgsConstructor
public class PresentismoHorasEtecController {

    private final PresentismoHorasEtecService service;

    @GetMapping("/legajo/{legajoId}/periodo/{anho}/{mes}")
    public ResponseEntity<EtecPresentismoHorasDto> findByLegajoAndPeriodo(
            @PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes) {
        return ResponseEntity.ok(service.findByLegajoAndPeriodo(legajoId, anho, mes));
    }

    @GetMapping("/periodo/{anho}/{mes}")
    public ResponseEntity<List<EtecPresentismoHorasDto>> findAllByPeriodo(
            @PathVariable Integer anho, @PathVariable Integer mes) {
        return ResponseEntity.ok(service.findAllByPeriodo(anho, mes));
    }

    @GetMapping("/{presentismoHorasId}")
    public ResponseEntity<EtecPresentismoHorasDto> findById(
            @PathVariable Long presentismoHorasId) {
        return ResponseEntity.ok(service.findById(presentismoHorasId));
    }

    @PostMapping("/")
    public ResponseEntity<EtecPresentismoHorasDto> create(
            @Valid @RequestBody PresentismoHorasRequest request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{presentismoHorasId}")
    public ResponseEntity<EtecPresentismoHorasDto> update(
            @PathVariable Long presentismoHorasId,
            @Valid @RequestBody PresentismoHorasUpdateRequest request) {
        return ResponseEntity.ok(service.update(presentismoHorasId, request));
    }

    @DeleteMapping("/{presentismoHorasId}")
    public ResponseEntity<Void> delete(@PathVariable Long presentismoHorasId) {
        service.delete(presentismoHorasId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
