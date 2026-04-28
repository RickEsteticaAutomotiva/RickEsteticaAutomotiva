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
        return Pessoa.builder().id(1L).nome("João Silva").cpf("123.456.789-00").email("joao@email.com")
                .telefone("11999999999").dataNascimento(LocalDate.of(1990, 1, 15)).senha("senhaEncodada")
                .roles(EnumSet.of(RoleEnum.ROLE_CLIENTE)).build();
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

    // ─── validaSenha ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar exceção quando senha for nula")
    void validaSenha_senhaNula_deveLancarExcecao() {
        Pessoa pessoa = pessoaMock();

        CampoInvalidoException exception = assertThrows(CampoInvalidoException.class, () -> pessoa.validaSenha(null));

        assertTrue(exception.getMensagem().contains("não pode ser nula ou vazia"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha for em branco")
    void validaSenha_senhaEmBranco_deveLancarExcecao() {
        Pessoa pessoa = pessoaMock();

        CampoInvalidoException exception = assertThrows(CampoInvalidoException.class, () -> pessoa.validaSenha("   "));

        assertTrue(exception.getMensagem().contains("não pode ser nula ou vazia"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha tem menos de 8 caracteres")
    void validaSenha_comprimentoMenor8_deveLancarExcecao() {
        Pessoa pessoa = pessoaMock();

        CampoInvalidoException exception = assertThrows(CampoInvalidoException.class,
                () -> pessoa.validaSenha("Abc@12"));

        assertTrue(exception.getMensagem().contains("Mínimo de 8 caracteres"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha não tem letra maiúscula")
    void validaSenha_semMaiuscula_deveLancarExcecao() {
        Pessoa pessoa = pessoaMock();

        CampoInvalidoException exception = assertThrows(CampoInvalidoException.class,
                () -> pessoa.validaSenha("abcdef123@"));

        assertTrue(exception.getMensagem().contains("Pelo menos 1 letra maiúscula"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha não tem letra minúscula")
    void validaSenha_semMinuscula_deveLancarExcecao() {
        Pessoa pessoa = pessoaMock();

        CampoInvalidoException exception = assertThrows(CampoInvalidoException.class,
                () -> pessoa.validaSenha("ABCDEF123@"));

        assertTrue(exception.getMensagem().contains("Pelo menos 1 letra minúscula"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha não tem número")
    void validaSenha_semNumero_deveLancarExcecao() {
        Pessoa pessoa = pessoaMock();

        CampoInvalidoException exception = assertThrows(CampoInvalidoException.class,
                () -> pessoa.validaSenha("AbCdEfgh@"));

        assertTrue(exception.getMensagem().contains("Pelo menos 1 número"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha não tem caractere especial")
    void validaSenha_semEspecial_deveLancarExcecao() {
        Pessoa pessoa = pessoaMock();

        CampoInvalidoException exception = assertThrows(CampoInvalidoException.class,
                () -> pessoa.validaSenha("Abcdefgh123"));

        assertTrue(exception.getMensagem().contains("Pelo menos 1 caractere especial"));
    }

    @Test
    @DisplayName("Deve aceitar senha forte com todos os requisitos")
    void validaSenha_senhaForte_naoDeveLancarExcecao() {
        Pessoa pessoa = pessoaMock();

        assertDoesNotThrow(() -> pessoa.validaSenha("Senha123@Forte"));
    }

    @Test
    @DisplayName("Deve aceitar senha forte com diversos caracteres especiais")
    void validaSenha_senhaForteComEspeciais_naoDeveLancarExcecao() {
        Pessoa pessoa = pessoaMock();

        assertDoesNotThrow(() -> pessoa.validaSenha("P@ssw0rd!Segura"));
        assertDoesNotThrow(() -> pessoa.validaSenha("Test#2024_Senha"));
        assertDoesNotThrow(() -> pessoa.validaSenha("Complex(Pwd)456"));
    }

    @Test
    @DisplayName("Deve lançar exceção com múltiplos requisitos não atendidos")
    void validaSenha_multiploErros_deveListarTodosErros() {
        Pessoa pessoa = pessoaMock();

        CampoInvalidoException exception = assertThrows(CampoInvalidoException.class,
                () -> pessoa.validaSenha("abc123"));

        String mensagem = exception.getMensagem();
        assertTrue(mensagem.contains("Mínimo de 8 caracteres"));
        assertTrue(mensagem.contains("Pelo menos 1 letra maiúscula"));
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
        Pessoa pessoa = Pessoa.builder().id(2L).nome("Gerente").email("gerente@email.com").senha("hash")
                .roles(EnumSet.of(RoleEnum.ROLE_GERENTE, RoleEnum.ROLE_CLIENTE)).build();

        assertTrue(pessoa.temRole(RoleEnum.ROLE_GERENTE));
        assertTrue(pessoa.temRole(RoleEnum.ROLE_CLIENTE));
        assertFalse(pessoa.temRole(RoleEnum.ROLE_ADMIN));
    }

    @Test
    @DisplayName("Builder sem roles explícitas deve inicializar com ROLE_CLIENTE por padrão")
    void builder_semRoles_deveUsarRoleUserComoDefault() {
        Pessoa pessoa = Pessoa.builder().id(3L).nome("Default").email("default@email.com").senha("hash").build();

        assertNotNull(pessoa.getRoles());
        assertTrue(pessoa.getRoles().contains(RoleEnum.ROLE_CLIENTE));
    }
}
