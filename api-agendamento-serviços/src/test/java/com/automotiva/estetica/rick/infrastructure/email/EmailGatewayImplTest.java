package com.automotiva.estetica.rick.infrastructure.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.Email;
import com.automotiva.estetica.rick.domain.enums.StatusEmailEnum;
import com.automotiva.estetica.rick.infrastructure.repository.email.EmailRepository;
import com.automotiva.estetica.rick.infrastructure.entity.EmailEntity;
import com.automotiva.estetica.rick.infrastructure.mapper.EmailEntityMapper;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class EmailGatewayImplTest {

    @Mock
    private JavaMailSender sender;

    @Mock
    private EmailRepository emailRepository;

    @Mock
    private EmailEntityMapper emailEntityMapper;

    @InjectMocks
    private EmailGatewayImpl emailGateway;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailGateway, "emailOrigem", "noreply@rick.com");
        ReflectionTestUtils.setField(emailGateway, "nomeOrigem", "Rick Estetica Automotiva");
    }

    @Test
    void deveEnviarEmailComSucessoEPersistirNoFinally() {
        Email email = Email.builder().destinatario("cliente@teste.com").assunto("Assunto").corpo("Corpo").build();
        EmailEntity entity = new EmailEntity();
        MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()));

        when(sender.createMimeMessage()).thenReturn(mimeMessage);
        when(emailEntityMapper.toEntity(email)).thenReturn(entity);

        emailGateway.enviar(email);

        assertEquals(StatusEmailEnum.SENT, email.getStatusEmail());
        assertNotNull(email.getDataEnvioEmail());
        assertEquals("noreply@rick.com", email.getRemetente());
        verify(sender).send(mimeMessage);
        verify(emailRepository).save(entity);
    }

    @Test
    void deveMarcarErroELancarExcecaoQuandoFalharEnvioMasPersistirNoFinally() {
        Email email = Email.builder().destinatario("cliente@teste.com").assunto("Assunto").corpo("Corpo").build();
        EmailEntity entity = new EmailEntity();
        MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()));

        when(sender.createMimeMessage()).thenReturn(mimeMessage);
        when(emailEntityMapper.toEntity(email)).thenReturn(entity);
        org.mockito.Mockito.doThrow(new MailSendException("smtp down")).when(sender).send(any(MimeMessage.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> emailGateway.enviar(email));

        assertEquals(StatusEmailEnum.ERROR, email.getStatusEmail());
        assertNotNull(email.getDataEnvioEmail());
        assertEquals("noreply@rick.com", email.getRemetente());
        assertNotNull(exception.getMessage());
        verify(emailRepository).save(entity);
    }

    @Test
    void deveEnviarComCcBccEAnexosIncluindoNomePadrao() throws Exception {
        Email email = Email.builder().destinatario("a@x.com;b@y.com").comCopia("cc@x.com").comCopiaOculta("bcc@x.com")
                .assunto("Assunto com anexos").corpo("Corpo HTML").build();
        EmailEntity entity = new EmailEntity();
        MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()));
        MultipartFile anexoComNome = org.mockito.Mockito.mock(MultipartFile.class);
        MultipartFile anexoSemNome = org.mockito.Mockito.mock(MultipartFile.class);

        when(sender.createMimeMessage()).thenReturn(mimeMessage);
        when(emailEntityMapper.toEntity(email)).thenReturn(entity);
        when(anexoComNome.getOriginalFilename()).thenReturn("arquivo.pdf");
        when(anexoComNome.getBytes()).thenReturn("pdf".getBytes());
        when(anexoSemNome.getOriginalFilename()).thenReturn(null);
        when(anexoSemNome.getBytes()).thenReturn("bin".getBytes());

        emailGateway.enviarEmailComAnexos(email, List.of(anexoComNome, anexoSemNome));

        assertEquals(StatusEmailEnum.SENT, email.getStatusEmail());
        assertEquals(2, email.getAnexos().size());
        assertEquals(2, email.getNomesAnexos().size());
        assertEquals("arquivo.pdf", email.getNomesAnexos().getFirst());
        assertEquals("anexo.bin", email.getNomesAnexos().get(1));
        assertEquals(2, mimeMessage.getRecipients(Message.RecipientType.TO).length);
        assertEquals(1, mimeMessage.getRecipients(Message.RecipientType.CC).length);
        assertEquals(1, mimeMessage.getRecipients(Message.RecipientType.BCC).length);
        verify(sender).send(mimeMessage);
        verify(emailRepository).save(entity);
    }

    @Test
    void deveEnviarComDestinatarioEVaziosSemAplicarCcBccNemAnexos() {
        Email email = Email.builder().destinatario("").comCopia("").comCopiaOculta("").assunto("Assunto").corpo("Corpo")
                .build();
        EmailEntity entity = new EmailEntity();
        MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()));

        when(sender.createMimeMessage()).thenReturn(mimeMessage);
        when(emailEntityMapper.toEntity(email)).thenReturn(entity);

        emailGateway.enviarEmailComAnexos(email, Collections.emptyList());

        assertEquals(StatusEmailEnum.SENT, email.getStatusEmail());
        assertEquals("noreply@rick.com", email.getRemetente());
        verify(sender).send(mimeMessage);
        verify(emailRepository).save(entity);
    }

    @Test
    void deveEnviarComDestinatarioNuloSemAplicarTo() {
        Email email = Email.builder().destinatario(null).comCopia(null).comCopiaOculta(null).assunto("Assunto")
                .corpo("Corpo").build();
        EmailEntity entity = new EmailEntity();
        MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()));

        when(sender.createMimeMessage()).thenReturn(mimeMessage);
        when(emailEntityMapper.toEntity(email)).thenReturn(entity);

        emailGateway.enviarEmailComAnexos(email, null);

        assertEquals(StatusEmailEnum.SENT, email.getStatusEmail());
        verify(sender).send(mimeMessage);
        verify(emailRepository).save(entity);
    }

    @Test
    void deveMarcarErroQuandoFalharLeituraDeAnexoComIOException() throws Exception {
        Email email = Email.builder().destinatario("a@x.com").assunto("Assunto").corpo("Corpo").build();
        EmailEntity entity = new EmailEntity();
        MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()));
        MultipartFile anexo = org.mockito.Mockito.mock(MultipartFile.class);

        when(sender.createMimeMessage()).thenReturn(mimeMessage);
        when(emailEntityMapper.toEntity(email)).thenReturn(entity);
        when(anexo.getOriginalFilename()).thenReturn("a.txt");
        when(anexo.getBytes()).thenThrow(new IOException("falha leitura"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> emailGateway.enviarEmailComAnexos(email, List.of(anexo)));

        assertEquals(StatusEmailEnum.ERROR, email.getStatusEmail());
        assertNotNull(ex.getMessage());
        verify(emailRepository).save(entity);
        verify(sender, never()).send(mimeMessage);
    }
}
