package com.automotiva.estetica.rick.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import com.automotiva.estetica.rick.domain.exception.CampoInvalidoException;
import java.time.LocalDate;
import java.util.EnumSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PessoaTest {

    private Pessoa pessoaMock() {
        return Pessoa.builder()
                .id(1L)
                .nome("João Silva")
                .cpf("123.456.789-00")
                .email("joao@email.com")
                .telefone("11999999999")
                .dataNascimento(LocalDate.of(1990, 1, 15))
                .senha("senhaEncodada")
                .roles(EnumSet.of(RoleEnum.ROLE_CLIENTE))
                .build();
    }

    @Test
    @DisplayName("Deve atualizar apenas campos não nulos")
    void atualizar_apenasNaoNulos() {
        Pessoa pessoa = pessoaMock();

        pessoa.atualizar("Maria Silva", null, null, "11988888888", null);

        assertEquals("Maria Silva", pessoa.getNome());
        assertEquals("123.456.789-00", pessoa.getCpf());
        assertEquals("joao@email.com", pessoa.getEmail());
        assertEquals("11988888888", pessoa.getTelefone());
        assertEquals(LocalDate.of(1990, 1, 15), pessoa.getDataNascimento());
    }

    @Test
    @DisplayName("Deve atualizar todos os campos quando todos forem fornecidos")
    void atualizar_todosCampos() {
        Pessoa pessoa = pessoaMock();
        LocalDate novaData = LocalDate.of(1995, 6, 20);

        pessoa.atualizar("Carlos", "987.654.321-00", "carlos@email.com", "11977777777", novaData);

        assertEquals("Carlos", pessoa.getNome());
        assertEquals("987.654.321-00", pessoa.getCpf());
        assertEquals("carlos@email.com", pessoa.getEmail());
        assertEquals("11977777777", pessoa.getTelefone());
        assertEquals(novaData, pessoa.getDataNascimento());
    }

    @Test
    @DisplayName("Deve lançar CampoInvalidoException quando senhaAtual for nula")
    void validarDadosSenha_senhaAtualNula_deveLancarExcecao() {
        Pessoa pessoa = pessoaMock();

        assertThrows(CampoInvalidoException.class, () -> pessoa.validarDadosSenha(null, "novaSenha"));
    }

    @Test
    @DisplayName("Deve lançar CampoInvalidoException quando senhaAtual for em branco")
    void validarDadosSenha_senhaAtualEmBranco_deveLancarExcecao() {
        Pessoa pessoa = pessoaMock();

        assertThrows(CampoInvalidoException.class, () -> pessoa.validarDadosSenha("  ", "novaSenha"));
    }

    @Test
    @DisplayName("Deve lançar CampoInvalidoException quando novaSenha for nula")
    void validarDadosSenha_novaSenhaNula_deveLancarExcecao() {
        Pessoa pessoa = pessoaMock();

        assertThrows(CampoInvalidoException.class, () -> pessoa.validarDadosSenha("senhaAtual", null));
    }

    @Test
    @DisplayName("Deve lançar CampoInvalidoException quando novaSenha for em branco")
    void validarDadosSenha_novaSenhaEmBranco_deveLancarExcecao() {
        Pessoa pessoa = pessoaMock();

        assertThrows(CampoInvalidoException.class, () -> pessoa.validarDadosSenha("senhaAtual", "   "));
    }

    @Test
    @DisplayName("Não deve lançar exceção quando ambas as senhas forem válidas")
    void validarDadosSenha_dadosValidos_naoDeveLancarExcecao() {
        Pessoa pessoa = pessoaMock();

        assertDoesNotThrow(() -> pessoa.validarDadosSenha("senhaAtual", "novaSenha123"));
    }

    @Test
    @DisplayName("Deve alterar a senha corretamente")
    void alterarSenha_devePersistirNovaSenha() {
        Pessoa pessoa = pessoaMock();

        pessoa.alterarSenha("novaSenhaEncodada");

        assertEquals("novaSenhaEncodada", pessoa.getSenha());
    }

    // ─── roles ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("temRole deve retornar true quando a role está presente")
    void temRole_rolePresente_deveRetornarTrue() {
        Pessoa pessoa = pessoaMock(); // ROLE_CLIENTE

        assertTrue(pessoa.temRole(RoleEnum.ROLE_CLIENTE));
    }

    @Test
    @DisplayName("temRole deve retornar false quando a role não está presente")
    void temRole_roleAusente_deveRetornarFalse() {
        Pessoa pessoa = pessoaMock(); // ROLE_CLIENTE

        assertFalse(pessoa.temRole(RoleEnum.ROLE_ADMIN));
        assertFalse(pessoa.temRole(RoleEnum.ROLE_GERENTE));
    }

    @Test
    @DisplayName("temRole deve suportar múltiplas roles")
    void temRole_multiRole_deveReconhecerAmbas() {
        Pessoa pessoa = Pessoa.builder()
                .id(2L)
                .nome("Gerente")
                .email("gerente@email.com")
                .senha("hash")
                .roles(EnumSet.of(RoleEnum.ROLE_GERENTE, RoleEnum.ROLE_CLIENTE))
                .build();

        assertTrue(pessoa.temRole(RoleEnum.ROLE_GERENTE));
        assertTrue(pessoa.temRole(RoleEnum.ROLE_CLIENTE));
        assertFalse(pessoa.temRole(RoleEnum.ROLE_ADMIN));
    }

    @Test
    @DisplayName("Builder sem roles explícitas deve inicializar com ROLE_CLIENTE por padrão")
    void builder_semRoles_deveUsarRoleUserComoDefault() {
        Pessoa pessoa = Pessoa.builder()
                .id(3L)
                .nome("Default")
                .email("default@email.com")
                .senha("hash")
                .build();

        assertNotNull(pessoa.getRoles());
        assertTrue(pessoa.getRoles().contains(RoleEnum.ROLE_CLIENTE));
    }
}
