package com.automotiva.estetica.rick.domain.usecase;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class PeriodoMensalFactory {

    private PeriodoMensalFactory() {
    }

    public static PeriodoMensal mesAtual() {
        return PeriodoMensal.mesAtual();
    }

    public static PeriodoMensal ultimos30Dias() {
        LocalDate inicio = LocalDate.now().minusDays(30);
        return new PeriodoMensal(inicio.atStartOfDay(), LocalDateTime.now());
    }

    public static PeriodoMensal mesAnterior() {
        return PeriodoMensal.mesAnterior();
    }
}
