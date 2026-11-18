package um.haberes.etec.generacion.application;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import um.haberes.etec.client.core.CoreCargoLiquidacionClient;
import um.haberes.etec.client.core.CoreCategoriaClient;
import um.haberes.etec.client.core.CoreControlEtecClient;
import um.haberes.etec.client.core.dto.CargoLiquidacionDto;
import um.haberes.etec.client.core.dto.CargoLiquidacionRequestDto;
import um.haberes.etec.client.core.dto.CategoriaDto;
import um.haberes.etec.client.core.dto.ControlEtecDto;
import um.haberes.etec.shared.exception.EtecValidationException;

/**
 * Circuito ETEC de cargos de liquidación.
 *
 * <p>La regla {@code basico = puntos × valor del punto} y la escritura de
 * {@code cargo_liquidacion} viven en haberes-core, que es el dueño de la tabla y de la
 * transacción. Acá quedan las validaciones de negocio (categorías configuradas y valor del punto
 * del período) y el mapeo a los DTOs del servicio.</p>
 */
@Service
@RequiredArgsConstructor
public class GeneracionCargoLiquidacionService {

    private final CoreControlEtecClient controlEtecClient;

    private final CoreCategoriaClient categoriaClient;

    private final CoreCargoLiquidacionClient cargoLiquidacionClient;

    public List<CargoLiquidacionRequestDto> preview(Integer anho, Integer mes) {
        validarPeriodo(anho, mes);
        return aRequest(cargoLiquidacionClient.previewEtecByPeriodo(anho, mes));
    }

    public List<CargoLiquidacionRequestDto> previewByLegajo(Long legajoId, Integer anho, Integer mes) {
        validarPeriodo(anho, mes);
        return aRequest(cargoLiquidacionClient.previewEtecByLegajo(legajoId, anho, mes));
    }

    public List<CargoLiquidacionDto> generar(Integer anho, Integer mes) {
        validarPeriodo(anho, mes);
        return cargoLiquidacionClient.generarEtecByPeriodo(anho, mes);
    }

    public List<CargoLiquidacionDto> generarByLegajo(Long legajoId, Integer anho, Integer mes) {
        validarPeriodo(anho, mes);
        return cargoLiquidacionClient.generarEtecByLegajo(legajoId, anho, mes);
    }

    public List<CargoLiquidacionDto> findByLegajo(Long legajoId, Integer anho, Integer mes) {
        Set<Integer> categoriaIds = categoriasConPuntos().keySet();
        if (categoriaIds.isEmpty()) {
            return List.of();
        }
        return cargoLiquidacionClient.findByLegajo(legajoId, anho, mes).stream()
                .filter(cargo -> categoriaIds.contains(cargo.getCategoriaId()))
                .toList();
    }

    public List<CargoLiquidacionDto> findByPeriodo(Integer anho, Integer mes) {
        Set<Integer> categoriaIds = categoriasConPuntos().keySet();
        if (categoriaIds.isEmpty()) {
            return List.of();
        }
        return cargoLiquidacionClient.findByPeriodo(anho, mes).stream()
                .filter(cargo -> categoriaIds.contains(cargo.getCategoriaId()))
                .toList();
    }

    private void validarPeriodo(Integer anho, Integer mes) {
        if (categoriasConPuntos().isEmpty()) {
            throw new EtecValidationException("No hay cargos ETEC con puntos configurados");
        }
        ControlEtecDto control = controlEtecClient.findByPeriodoOpcional(anho, mes).getBody();
        if (control == null || control.getValorPuntoEtec() == null
                || control.getValorPuntoEtec().compareTo(BigDecimal.ZERO) <= 0) {
            throw new EtecValidationException(
                    "El período " + anho + "/" + mes + " no tiene un valor de punto ETEC mayor a cero");
        }
    }

    private Map<Integer, CategoriaDto> categoriasConPuntos() {
        Map<Integer, CategoriaDto> categorias = new LinkedHashMap<>();
        for (CategoriaDto categoria : categoriaClient.findConPuntos()) {
            if (categoria.getCategoriaId() != null) {
                categorias.putIfAbsent(categoria.getCategoriaId(), categoria);
            }
        }
        return categorias;
    }

    private List<CargoLiquidacionRequestDto> aRequest(List<CargoLiquidacionDto> cargos) {
        return cargos.stream()
                .map(cargo -> CargoLiquidacionRequestDto.builder()
                        .legajoId(cargo.getLegajoId())
                        .anho(cargo.getAnho())
                        .mes(cargo.getMes())
                        .dependenciaId(cargo.getDependenciaId())
                        .fechaDesde(cargo.getFechaDesde())
                        .fechaHasta(cargo.getFechaHasta())
                        .categoriaId(cargo.getCategoriaId())
                        .categoriaNombre(cargo.getCategoriaNombre())
                        .categoriaBasico(cargo.getCategoriaBasico())
                        .estadoDocente(cargo.getEstadoDocente())
                        .horasJornada(cargo.getHorasJornada())
                        .jornada(cargo.getJornada())
                        .presentismo(cargo.getPresentismo())
                        .situacion(cargo.getSituacion())
                        .build())
                .toList();
    }
}
