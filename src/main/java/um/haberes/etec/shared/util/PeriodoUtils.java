package um.haberes.etec.shared.util;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class PeriodoUtils {

    private PeriodoUtils() {
    }

    public static OffsetDateTime firstDay(Integer anho, Integer mes) {
        return OffsetDateTime.of(anho, mes, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    }

    public static OffsetDateTime lastDay(Integer anho, Integer mes) {
        return firstDay(anho, mes).plusMonths(1).minusDays(1);
    }
}
