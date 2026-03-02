package com.automotiva.estetica.rick.adapter.out.calendar;

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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!integration-test")
public class GoogleCalendarConexao {

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
            log.info("Google Calendar inicializado com sucesso");
        } catch (IOException | GeneralSecurityException e) {
            log.error("Falha ao inicializar Google Calendar", e);
            throw new RuntimeException("Falha ao inicializar Google Calendar", e);
        }
    }

    public Calendar obterServico() {
        return servicoCalendario;
    }

    private Calendar criarServicoCalendario() throws IOException, GeneralSecurityException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credencial = obterCredenciais(httpTransport);
        return new Calendar.Builder(httpTransport, JSON_FACTORY, credencial)
                .setApplicationName(nomeAplicacao)
                .build();
    }

    private Credential obterCredenciais(final NetHttpTransport httpTransport) throws IOException {
        InputStream in = new ClassPathResource(arquivoCredenciais.replace("classpath:", "")).getInputStream();
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                        httpTransport, JSON_FACTORY, clientSecrets, ESCOPOS)
                .setDataStoreFactory(new FileDataStoreFactory(new File(caminhoTokens)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver =
                new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
