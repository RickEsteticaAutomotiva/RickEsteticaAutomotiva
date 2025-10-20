package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.entity.EmailEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.enums.StatusEmailEnum;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.EmailConfig;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.EmailRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class EmailService {

    private final EmailRepository emailRepository;
    private final JavaMailSender sender;
    private final EmailConfig emailConfig;

    public void enviarEmailComAnexos(EmailEntity email, List<MultipartFile> anexos) {
        email.setDataEnvioEmail(LocalDateTime.now());

        try {
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            email.setRemetente(emailConfig.getEmailOrigem());
            helper.setFrom(email.getRemetente(), emailConfig.getNomeOrigem());

            if (email.getDestinatario() != null && !email.getDestinatario().isEmpty()) {
                helper.setTo(email.getDestinatario().split(";"));
            }

            if (email.getComCopia() != null && !email.getComCopia().isEmpty()) {
                helper.setCc(email.getComCopia().split(";"));
            }

            if (email.getComCopiaOculta() != null && !email.getComCopiaOculta().isEmpty()) {
                helper.setBcc(email.getComCopiaOculta().split(";"));
            }

            helper.setSubject(email.getAssunto());
            helper.setText(email.getCorpo(), true);

            if (anexos != null && !anexos.isEmpty()) {
                List<byte[]> bytesAnexos = new ArrayList<>();
                List<String> nomesAnexos = new ArrayList<>();

                for (MultipartFile anexo : anexos) {
                    helper.addAttachment(anexo.getOriginalFilename(), anexo);
                    bytesAnexos.add(anexo.getBytes());
                    nomesAnexos.add(anexo.getOriginalFilename());
                }

                email.setAnexos(bytesAnexos);
                email.setNomesAnexos(nomesAnexos);
            }

            sender.send(mimeMessage);
            email.setStatusEmail(StatusEmailEnum.SENT);

        } catch (MailException | MessagingException | IOException e) {
            email.setStatusEmail(StatusEmailEnum.ERROR);
            log.error(e.getMessage());
            //TODO criar uma exeption de erro
            throw new RuntimeException("Erro ao enviar email: " + e.getMessage());
        } finally {
            emailRepository.save(email);
        }
    }
}