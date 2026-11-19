package um.haberes.etec.cargo.infrastructure.web;

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
import um.haberes.etec.cargo.application.CargoEtecService;
import um.haberes.etec.cargo.infrastructure.web.dto.CargoEtecRequest;
import um.haberes.etec.client.core.dto.CategoriaDto;

@RestController
@RequestMapping("/api/haberes/etec/cargo")
@RequiredArgsConstructor
public class CargoEtecController {

    private final CargoEtecService service;

    @GetMapping("/")
    public ResponseEntity<List<CategoriaDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{categoriaId}")
    public ResponseEntity<CategoriaDto> findById(@PathVariable Integer categoriaId) {
        return ResponseEntity.ok(service.findById(categoriaId));
    }

    @PostMapping("/{anho}/{mes}")
    public ResponseEntity<CategoriaDto> create(@PathVariable Integer anho, @PathVariable Integer mes,
            @Valid @RequestBody CargoEtecRequest request) {
        return new ResponseEntity<>(service.create(anho, mes, request), HttpStatus.OK);
    }

    @PutMapping("/{categoriaId}/{anho}/{mes}")
    public ResponseEntity<CategoriaDto> update(@PathVariable Integer categoriaId, @PathVariable Integer anho,
            @PathVariable Integer mes, @Valid @RequestBody CargoEtecRequest request) {
        return ResponseEntity.ok(service.update(categoriaId, anho, mes, request));
    }

    @DeleteMapping("/{categoriaId}")
    public ResponseEntity<Void> delete(@PathVariable Integer categoriaId) {
        service.delete(categoriaId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
