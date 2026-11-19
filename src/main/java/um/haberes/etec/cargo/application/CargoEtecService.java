package um.haberes.etec.cargo.application;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import um.haberes.etec.client.core.CoreCategoriaClient;
import um.haberes.etec.client.core.CoreEtecAsignacionCargoClient;
import um.haberes.etec.client.core.dto.CategoriaDto;
import um.haberes.etec.client.core.dto.CategoriaRequestDto;
import um.haberes.etec.cargo.infrastructure.web.dto.CargoEtecRequest;
import um.haberes.etec.shared.exception.EtecConflictException;
import um.haberes.etec.shared.exception.EtecValidationException;

/**
 * Catálogo de cargos ETEC. El cargo es una categoría de core con {@code puntos > 0} y
 * {@code docente = 1}; el importe de la categoría se deja en cero porque surge del cálculo
 * {@code puntos × valor del punto}. La persistencia la hace siempre haberes-core.
 */
@Service
@RequiredArgsConstructor
public class CargoEtecService {

    private static final int CATEGORIA_INICIAL = 9000;

    private final CoreCategoriaClient categoriaClient;
    private final CoreEtecAsignacionCargoClient asignacionClient;

    public List<CategoriaDto> findAll() {
        return categoriaClient.findConPuntos();
    }

    public CategoriaDto findById(Integer categoriaId) {
        return validarCargoEtec(categoriaClient.findById(categoriaId));
    }

    public CategoriaDto create(Integer anho, Integer mes, CargoEtecRequest request) {
        CategoriaDto last = categoriaClient.findLast();
        Integer nextId = last == null || last.getCategoriaId() == null
                ? CATEGORIA_INICIAL
                : last.getCategoriaId() + 1;
        CategoriaRequestDto body = CategoriaRequestDto.builder()
                .categoriaId(nextId)
                .nombre(request.getNombre().trim())
                .basico(BigDecimal.ZERO)
                .docente(1)
                .noDocente(0)
                .liquidaPorHora(0)
                .estadoDocente(BigDecimal.ZERO)
                .puntos(request.getPuntos())
                .build();
        return categoriaClient.add(body, anho, mes);
    }

    public CategoriaDto update(Integer categoriaId, Integer anho, Integer mes, CargoEtecRequest request) {
        CategoriaDto existing = validarCargoEtec(categoriaClient.findById(categoriaId));
        CategoriaRequestDto body = CategoriaRequestDto.builder()
                .categoriaId(existing.getCategoriaId())
                .nombre(request.getNombre().trim())
                .basico(BigDecimal.ZERO)
                .docente(1)
                .noDocente(0)
                .liquidaPorHora(0)
                .estadoDocente(BigDecimal.ZERO)
                .puntos(request.getPuntos())
                .build();
        return categoriaClient.update(body, categoriaId, anho, mes);
    }

    public void delete(Integer categoriaId) {
        validarCargoEtec(categoriaClient.findById(categoriaId));
        if (Boolean.TRUE.equals(asignacionClient.existsByCategoria(categoriaId))) {
            throw new EtecConflictException(
                    "No se puede borrar el cargo " + categoriaId + ": tiene asignaciones registradas");
        }
        categoriaClient.delete(categoriaId);
    }

    private CategoriaDto validarCargoEtec(CategoriaDto categoria) {
        if (categoria == null) {
            throw new EtecValidationException("El cargo no existe");
        }
        if (categoria.getPuntos() == null || categoria.getPuntos().compareTo(BigDecimal.ZERO) <= 0) {
            throw new EtecValidationException(
                    "La categoría " + categoria.getCategoriaId() + " no es un cargo con puntos");
        }
        if (categoria.getDocente() == null || categoria.getDocente() != 1) {
            throw new EtecValidationException(
                    "La categoría " + categoria.getCategoriaId() + " no está marcada como docente");
        }
        return categoria;
    }
}
