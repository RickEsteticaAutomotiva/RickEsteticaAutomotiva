package com.automotiva.estetica.rick.infrastructure.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.automotiva.estetica.rick.domain.entity.Email;
import com.automotiva.estetica.rick.domain.enums.StatusEmailEnum;
import com.automotiva.estetica.rick.infrastructure.entity.EmailEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

class EmailEntityMapperTest {

    private final EmailEntityMapper mapper = Mappers.getMapper(EmailEntityMapper.class);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mapper, "pessoaEntityMapper", Mappers.getMapper(PessoaEntityMapper.class));
    }

    @Test
    void toDomain_deveMapearCamposBasicosEListasTransientes() {
        EmailEntity entity = EmailEntity.builder().remetente("a@a.com").destinatario("b@b.com").assunto("Assunto")
                .corpo("Corpo").statusEmail(StatusEmailEnum.SENT).dataEnvioEmail(LocalDateTime.now())
                .anexos(List.of("txt".getBytes())).nomesAnexos(List.of("a.txt")).build();

        Email domain = mapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals("a@a.com", domain.getRemetente());
        assertEquals("Assunto", domain.getAssunto());
        assertEquals(StatusEmailEnum.SENT, domain.getStatusEmail());
        assertEquals(1, domain.getAnexos().size());
        assertEquals(1, domain.getNomesAnexos().size());
    }

    @Test
    void toEntity_deveMapearCamposBasicosEListasTransientes() {
        Email domain = Email.builder().remetente("x@x.com").destinatario("y@y.com").assunto("Ok").corpo("Body")
                .statusEmail(StatusEmailEnum.ERROR).dataEnvioEmail(LocalDateTime.now())
                .anexos(List.of("pdf".getBytes())).nomesAnexos(List.of("b.pdf")).build();

        EmailEntity entity = mapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals("x@x.com", entity.getRemetente());
        assertEquals("Ok", entity.getAssunto());
        assertEquals(StatusEmailEnum.ERROR, entity.getStatusEmail());
        assertEquals(1, entity.getAnexos().size());
        assertEquals(1, entity.getNomesAnexos().size());
    }

    @Test
    void toDomain_quandoNulo_deveRetornarNulo() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    void toEntity_quandoNulo_deveRetornarNulo() {
        assertNull(mapper.toEntity(null));
    }
}
