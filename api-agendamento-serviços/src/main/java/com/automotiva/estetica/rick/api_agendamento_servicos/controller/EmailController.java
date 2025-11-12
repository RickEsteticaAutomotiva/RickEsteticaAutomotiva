package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.entity.EmailEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.EmailService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/emails")
@AllArgsConstructor
// TODO clase para teste
public class EmailController {

    private final EmailService emailService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> enviarEmail(
            @RequestPart("email") String emailJson,
            @RequestPart(value = "anexos", required = false) MultipartFile[] anexos) {

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        EmailEntity email;
        try {
            email = mapper.readValue(emailJson, EmailEntity.class);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        List<MultipartFile> listaAnexos = anexos == null ? Collections.emptyList() : Arrays.asList(anexos);

        emailService.enviarEmailComAnexos(email, listaAnexos);
        return ResponseEntity.accepted().build();
    }
}
