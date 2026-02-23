package com.automotiva.estetica.rick.adapter.out.email;

import com.automotiva.estetica.rick.application.port.out.EmailPort;
import com.automotiva.estetica.rick.application.port.out.EmailRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.Email;
import com.automotiva.estetica.rick.domain.enums.StatusEmailEnum;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailAdapter implements EmailPort {

    private final JavaMailSender sender;
    private final EmailRepositoryPort emailRepositoryPort;

    @Value("${spring.mail.username}")
    private String emailOrigem;

    @Value("${email.nome-origem:Rick Estética Automotiva}")
    private String nomeOrigem;

    @Override
    public void enviarEmailComAnexos(Email email, List<MultipartFile> anexos) {
        email.setDataEnvioEmail(LocalDateTime.now());
        try {
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            email.setRemetente(emailOrigem);
            helper.setFrom(email.getRemetente(), nomeOrigem);

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
            log.error("Erro ao enviar e-mail: {}", e.getMessage());
            throw new RuntimeException("Erro ao enviar e-mail: " + e.getMessage(), e);
        } finally {
            emailRepositoryPort.salvar(email);
        }
    }
}
