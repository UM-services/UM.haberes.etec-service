package um.haberes.etec.generacion.infrastructure.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import um.haberes.etec.client.core.dto.CargoLiquidacionDto;
import um.haberes.etec.client.core.dto.CargoLiquidacionRequestDto;
import um.haberes.etec.generacion.application.GeneracionCargoLiquidacionService;

@RestController
@RequestMapping("/api/haberes/etec/cargo-liquidacion")
@RequiredArgsConstructor
public class GeneracionCargoLiquidacionController {

    private final GeneracionCargoLiquidacionService service;

    @PostMapping("/generar/{anho}/{mes}")
    public ResponseEntity<List<CargoLiquidacionDto>> generar(@PathVariable Integer anho,
            @PathVariable Integer mes) {
        return new ResponseEntity<>(service.generar(anho, mes), HttpStatus.OK);
    }

    @PostMapping("/generar/legajo/{legajoId}/{anho}/{mes}")
    public ResponseEntity<List<CargoLiquidacionDto>> generarByLegajo(@PathVariable Long legajoId,
            @PathVariable Integer anho, @PathVariable Integer mes) {
        return new ResponseEntity<>(service.generarByLegajo(legajoId, anho, mes), HttpStatus.OK);
    }

    @GetMapping("/preview/{anho}/{mes}")
    public ResponseEntity<List<CargoLiquidacionRequestDto>> preview(@PathVariable Integer anho,
            @PathVariable Integer mes) {
        return ResponseEntity.ok(service.preview(anho, mes));
    }

    @GetMapping("/preview/legajo/{legajoId}/{anho}/{mes}")
    public ResponseEntity<List<CargoLiquidacionRequestDto>> previewByLegajo(@PathVariable Long legajoId,
            @PathVariable Integer anho, @PathVariable Integer mes) {
        return ResponseEntity.ok(service.previewByLegajo(legajoId, anho, mes));
    }

    @GetMapping("/legajo/{legajoId}/{anho}/{mes}")
    public ResponseEntity<List<CargoLiquidacionDto>> findByLegajo(@PathVariable Long legajoId,
            @PathVariable Integer anho, @PathVariable Integer mes) {
        return ResponseEntity.ok(service.findByLegajo(legajoId, anho, mes));
    }

    @GetMapping("/periodo/{anho}/{mes}")
    public ResponseEntity<List<CargoLiquidacionDto>> findByPeriodo(@PathVariable Integer anho,
            @PathVariable Integer mes) {
        return ResponseEntity.ok(service.findByPeriodo(anho, mes));
    }
}
