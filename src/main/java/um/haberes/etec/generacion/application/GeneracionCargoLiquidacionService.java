package um.haberes.etec.generacion.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import um.haberes.etec.client.core.CoreCargoLiquidacionClient;
import um.haberes.etec.client.core.CoreCategoriaClient;
import um.haberes.etec.client.core.CoreControlClient;
import um.haberes.etec.client.core.CoreEtecAsignacionCargoClient;
import um.haberes.etec.client.core.dto.CargoLiquidacionDto;
import um.haberes.etec.client.core.dto.CargoLiquidacionRequestDto;
import um.haberes.etec.client.core.dto.CategoriaDto;
import um.haberes.etec.client.core.dto.ControlDto;
import um.haberes.etec.client.core.dto.EtecAsignacionCargoDto;
import um.haberes.etec.client.core.dto.ReemplazoCargosRequestDto;
import um.haberes.etec.shared.exception.EtecValidationException;
import um.haberes.etec.shared.util.PeriodoUtils;

/**
 * Regla del cargo por puntos: {@code basico = puntos del cargo × valor del punto del período}.
 * El cálculo y la orquestación viven acá; la escritura de {@code cargo_liquidacion} la hace
 * haberes-core.
 */
@Service
@RequiredArgsConstructor
public class GeneracionCargoLiquidacionService {

    private static final String SITUACION_ACTIVA = "A";

    private final CoreControlClient controlClient;
    private final CoreCategoriaClient categoriaClient;
    private final CoreEtecAsignacionCargoClient asignacionClient;
    private final CoreCargoLiquidacionClient cargoLiquidacionClient;

    public List<CargoLiquidacionRequestDto> preview(Integer anho, Integer mes) {
        return construir(anho, mes, null);
    }

    public List<CargoLiquidacionRequestDto> previewByLegajo(Long legajoId, Integer anho, Integer mes) {
        return construir(anho, mes, legajoId);
    }

    public List<CargoLiquidacionDto> generar(Integer anho, Integer mes) {
        List<CargoLiquidacionRequestDto> cargos = construir(anho, mes, null);
        return cargoLiquidacionClient.replaceByPeriodo(reemplazo(cargos), anho, mes);
    }

    public List<CargoLiquidacionDto> generarByLegajo(Long legajoId, Integer anho, Integer mes) {
        List<CargoLiquidacionRequestDto> cargos = construir(anho, mes, legajoId);
        return cargoLiquidacionClient.replaceByLegajo(reemplazo(cargos), legajoId, anho, mes);
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

    private List<CargoLiquidacionRequestDto> construir(Integer anho, Integer mes, Long legajoId) {
        BigDecimal valorPunto = valorPunto(anho, mes);
        Map<Integer, CategoriaDto> categorias = categoriasConPuntos();
        if (categorias.isEmpty()) {
            throw new EtecValidationException("No hay cargos ETEC con puntos configurados");
        }

        List<EtecAsignacionCargoDto> asignaciones = legajoId == null
                ? asignacionClient.findVigentesByPeriodo(anho, mes)
                : asignacionClient.findVigentesByLegajoAndPeriodo(legajoId, anho, mes);

        OffsetDateTime fechaDesde = PeriodoUtils.firstDay(anho, mes);
        OffsetDateTime fechaHasta = PeriodoUtils.lastDay(anho, mes);

        return asignaciones.stream()
                .filter(asignacion -> categorias.containsKey(asignacion.getCategoriaId()))
                .map(asignacion -> {
                    CategoriaDto categoria = categorias.get(asignacion.getCategoriaId());
                    BigDecimal basico = categoria.getPuntos().multiply(valorPunto)
                            .setScale(2, RoundingMode.HALF_UP);
                    return CargoLiquidacionRequestDto.builder()
                            .legajoId(asignacion.getLegajoId())
                            .anho(anho)
                            .mes(mes)
                            .dependenciaId(asignacion.getDependenciaId())
                            .fechaDesde(fechaDesde)
                            .fechaHasta(fechaHasta)
                            .categoriaId(categoria.getCategoriaId())
                            .categoriaNombre(categoria.getNombre())
                            .categoriaBasico(basico)
                            .estadoDocente(BigDecimal.ZERO)
                            .horasJornada(BigDecimal.ZERO)
                            .jornada(1)
                            .presentismo(0)
                            .situacion(SITUACION_ACTIVA)
                            .build();
                })
                .toList();
    }

    private BigDecimal valorPunto(Integer anho, Integer mes) {
        ControlDto control = controlClient.findByPeriodo(anho, mes);
        if (control == null || control.getValorPuntoEtec() == null
                || control.getValorPuntoEtec().compareTo(BigDecimal.ZERO) <= 0) {
            throw new EtecValidationException(
                    "El período " + anho + "/" + mes + " no tiene un valor de punto ETEC mayor a cero");
        }
        return control.getValorPuntoEtec();
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

    private ReemplazoCargosRequestDto reemplazo(List<CargoLiquidacionRequestDto> cargos) {
        Set<Integer> alcance = new LinkedHashSet<>(categoriasConPuntos().keySet());
        List<Integer> asignadas = asignacionClient.findCategoriasAsignadas();
        if (asignadas != null) {
            alcance.addAll(asignadas);
        }
        if (alcance.isEmpty()) {
            throw new EtecValidationException("No hay categorías ETEC para reemplazar");
        }
        return ReemplazoCargosRequestDto.builder()
                .categoriaIds(new ArrayList<>(alcance))
                .cargos(cargos)
                .build();
    }
}
