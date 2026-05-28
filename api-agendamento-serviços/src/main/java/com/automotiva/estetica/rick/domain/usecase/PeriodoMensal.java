package com.automotiva.estetica.rick.domain.usecase;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PeriodoMensal(LocalDateTime inicio, LocalDateTime fim) {

    public static PeriodoMensal mesAtual() {
        LocalDate inicio = LocalDate.now().withDayOfMonth(1);
        return new PeriodoMensal(inicio.atStartOfDay(), LocalDateTime.now());
    }

    public static PeriodoMensal mesAnterior() {
        LocalDate inicio = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate fim = inicio.plusMonths(1).minusDays(1);
        return new PeriodoMensal(inicio.atStartOfDay(), fim.atTime(23, 59, 59));
    }
}
