package um.haberes.etec.asignacion_cargo.application;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import um.haberes.etec.asignacion_cargo.infrastructure.web.dto.AsignacionCargoRequest;
import um.haberes.etec.cargo.application.CargoEtecService;
import um.haberes.etec.client.core.CoreEtecAsignacionCargoClient;
import um.haberes.etec.client.core.dto.EtecAsignacionCargoDto;
import um.haberes.etec.client.core.dto.EtecAsignacionCargoRequestDto;
import um.haberes.etec.shared.exception.EtecConflictException;
import um.haberes.etec.shared.exception.EtecValidationException;

/**
 * Alta y baja de cargos con puntos. La tabla {@code etec_asignacion_cargo} la persiste
 * haberes-core; acá sólo se validan las reglas (cargo ETEC, fechas y solapes).
 */
@Service
@RequiredArgsConstructor
public class AsignacionCargoService {

    private final CoreEtecAsignacionCargoClient client;
    private final CargoEtecService cargoEtecService;

    public List<EtecAsignacionCargoDto> findAllByLegajo(Long legajoId) {
        return client.findByLegajo(legajoId);
    }

    public EtecAsignacionCargoDto findById(Long asignacionCargoId) {
        return client.findById(asignacionCargoId);
    }

    public List<EtecAsignacionCargoDto> findAllVigentesByPeriodo(Integer anho, Integer mes) {
        return client.findVigentesByPeriodo(anho, mes);
    }

    public List<EtecAsignacionCargoDto> findAllVigentesByLegajoAndPeriodo(Long legajoId, Integer anho, Integer mes) {
        return client.findVigentesByLegajoAndPeriodo(legajoId, anho, mes);
    }

    public EtecAsignacionCargoDto create(AsignacionCargoRequest request) {
        validar(request);
        validarSolape(request, null);
        return client.add(toClientRequest(request));
    }

    public EtecAsignacionCargoDto update(Long asignacionCargoId, AsignacionCargoRequest request) {
        validar(request);
        validarSolape(request, asignacionCargoId);
        return client.update(toClientRequest(request), asignacionCargoId);
    }

    public void delete(Long asignacionCargoId) {
        client.delete(asignacionCargoId);
    }

    private void validar(AsignacionCargoRequest request) {
        cargoEtecService.findById(request.getCategoriaId());
        if (request.getFechaBaja() != null && request.getFechaBaja().isBefore(request.getFechaAlta())) {
            throw new EtecValidationException("La fecha de baja no puede ser anterior a la fecha de alta");
        }
    }

    private void validarSolape(AsignacionCargoRequest request, Long excludedId) {
        List<EtecAsignacionCargoDto> existentes = client.findByLegajo(request.getLegajoId());
        for (EtecAsignacionCargoDto existente : existentes) {
            if (excludedId != null && excludedId.equals(existente.getAsignacionCargoId())) {
                continue;
            }
            if (!Objects.equals(existente.getCategoriaId(), request.getCategoriaId())) {
                continue;
            }
            if (solapa(existente, request)) {
                throw new EtecConflictException("Ya existe una asignación vigente del cargo "
                        + request.getCategoriaId() + " para el legajo " + request.getLegajoId());
            }
        }
    }

    private boolean solapa(EtecAsignacionCargoDto existente, AsignacionCargoRequest request) {
        OffsetDateTime altaExistente = existente.getFechaAlta();
        if (altaExistente == null) {
            return false;
        }
        OffsetDateTime bajaExistente = existente.getFechaBaja();
        OffsetDateTime altaNueva = request.getFechaAlta();
        OffsetDateTime bajaNueva = request.getFechaBaja();
        boolean existenteTocaNueva = bajaExistente == null || !bajaExistente.isBefore(altaNueva);
        boolean nuevaTocaExistente = bajaNueva == null || !bajaNueva.isBefore(altaExistente);
        return existenteTocaNueva && nuevaTocaExistente;
    }

    private EtecAsignacionCargoRequestDto toClientRequest(AsignacionCargoRequest request) {
        return EtecAsignacionCargoRequestDto.builder()
                .legajoId(request.getLegajoId())
                .categoriaId(request.getCategoriaId())
                .dependenciaId(request.getDependenciaId())
                .fechaAlta(request.getFechaAlta())
                .fechaBaja(request.getFechaBaja())
                .observaciones(request.getObservaciones())
                .build();
    }
}
