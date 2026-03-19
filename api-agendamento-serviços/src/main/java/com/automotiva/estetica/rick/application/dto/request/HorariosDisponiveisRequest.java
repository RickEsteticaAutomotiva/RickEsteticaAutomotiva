package com.automotiva.estetica.rick.application.dto.request;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

public record HorariosDisponiveisRequest(
        @RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
        @RequestParam("servicosIds") List<Long> servicosIds) {
}
