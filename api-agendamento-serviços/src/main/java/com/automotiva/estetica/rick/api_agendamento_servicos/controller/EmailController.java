package com.automotiva.estetica.rick.api_agendamento_servicos.controller;

import com.automotiva.estetica.rick.api_agendamento_servicos.entity.EmailEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/emails")
@AllArgsConstructor
//TODO clase para teste
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/enviar-com-anexos")
    public ResponseEntity<String> enviarEmailComAnexos(
            @RequestPart("email") EmailEntity email,
            @RequestPart(value = "anexos", required = false) List<MultipartFile> anexos) {
        emailService.enviarEmailComAnexos(email, anexos);
        return ResponseEntity.ok("Email com anexos enviado com sucesso!");
    }
}
