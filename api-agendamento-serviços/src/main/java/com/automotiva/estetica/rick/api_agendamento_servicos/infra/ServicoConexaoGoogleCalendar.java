package com.automotiva.estetica.rick.api_agendamento_servicos.infra;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/**
 * Serviço responsável pela conexão e autenticação com a API do Google Calendar
 */
@Slf4j
@Service
public class ServicoConexaoGoogleCalendar {

    @Value("${google.calendar.credentials.file:classpath:credentials-oauth.json}")
    private String arquivoCredenciais;

    @Value("${google.calendar.application.name:ApiAgendamentoServices}")
    private String nomeAplicacao;

    @Value("${google.calendar.tokens.directory.path:tokens}")
    private String caminhoTokens;

    private Calendar servicoCalendario;

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> ESCOPOS = Collections.singletonList(CalendarScopes.CALENDAR);

    @PostConstruct
    public void inicializar() {
        try {
            this.servicoCalendario = criarServicoCalendario();
            log.info("Serviço de conexão do Google Calendar inicializado com sucesso usando OAuth 2.0");
        } catch (IOException | GeneralSecurityException e) {
            log.error("Falha ao inicializar o serviço de conexão do Google Calendar", e);
            throw new RuntimeException("Falha ao inicializar o serviço de conexão do Google Calendar", e);
        }
    }

    /**
     * Obtém as credenciais de autenticação do Google
     */
    private Credential obterCredenciais(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        Resource recurso = new ClassPathResource(arquivoCredenciais.replace("classpath:", ""));
        if (!recurso.exists()) {
            throw new IOException("Arquivo de credenciais não encontrado: " + arquivoCredenciais);
        }

        GoogleClientSecrets segredosCliente;
        try (InputStream entrada = recurso.getInputStream()) {
            segredosCliente = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(entrada));
        }

        GoogleAuthorizationCodeFlow fluxoAutorizacao = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, segredosCliente, ESCOPOS)
                .setDataStoreFactory(new FileDataStoreFactory(new File(caminhoTokens)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receptor = new LocalServerReceiver.Builder()
                .setPort(8080)
                .setCallbackPath("/oauth2callback")
                .build();

        return new AuthorizationCodeInstalledApp(fluxoAutorizacao, receptor).authorize("user");
    }

    /**
     * Cria o serviço do Google Calendar
     */
    private Calendar criarServicoCalendario() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credencial = obterCredenciais(HTTP_TRANSPORT);

        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credencial)
                .setApplicationName(nomeAplicacao)
                .build();
    }

    /**
     * Obtém o serviço do Google Calendar
     */
    public Calendar obterServicoCalendario() {
        return servicoCalendario;
    }

    /**
     * Verifica se o serviço está disponível
     */
    public boolean estaDisponivel() {
        return servicoCalendario != null;
    }

    /**
     * Força a renovação das credenciais
     */
    public void renovarCredenciais() throws IOException, GeneralSecurityException {
        log.info("Renovando credenciais do Google Calendar...");
        this.servicoCalendario = criarServicoCalendario();
        log.info("Credenciais renovadas com sucesso");
    }
}
