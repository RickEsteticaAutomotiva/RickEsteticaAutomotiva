package com.automotiva.estetica.rick.application.assembler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.application.dto.response.ErroLogResponse;
import com.automotiva.estetica.rick.application.mapper.ErroLogDTOMapper;
import com.automotiva.estetica.rick.application.security.ErroLogRedactionService;
import com.automotiva.estetica.rick.domain.entity.ErroLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ErroLogResponseAssemblerTest {

    @Mock
    private ErroLogDTOMapper erroLogDTOMapper;

    @Mock
    private ErroLogRedactionService erroLogRedactionService;

    @InjectMocks
    private ErroLogResponseAssembler erroLogResponseAssembler;

    @Test
    @DisplayName("Deve delegar a redacao da resposta")
    void toRedactedResponse_deveDelegarRedacao() {
        ErroLog erroLog = ErroLog.builder().id(10L).build();
        ErroLogResponse raw = ErroLogResponse.builder().id(10L).usuarioEmail("usuario@dominio.com").build();
        ErroLogResponse redacted = ErroLogResponse.builder().id(10L).usuarioEmail("u***@dominio.com").build();

        when(erroLogDTOMapper.toResponse(erroLog)).thenReturn(raw);
        when(erroLogRedactionService.redact(raw)).thenReturn(redacted);

        ErroLogResponse response = erroLogResponseAssembler.toRedactedResponse(erroLog);

        assertEquals(10L, response.getId());
        assertEquals("u***@dominio.com", response.getUsuarioEmail());
        verify(erroLogRedactionService).redact(raw);
    }

    @Test
    @DisplayName("Deve retornar nulo quando mapper retornar nulo")
    void toRedactedResponse_mapperNulo_deveRetornarNulo() {
        ErroLog erroLog = ErroLog.builder().id(1L).build();
        when(erroLogDTOMapper.toResponse(erroLog)).thenReturn(null);
        when(erroLogRedactionService.redact(null)).thenReturn(null);

        ErroLogResponse response = erroLogResponseAssembler.toRedactedResponse(erroLog);

        assertNull(response);
        verify(erroLogRedactionService).redact(null);
    }
}
