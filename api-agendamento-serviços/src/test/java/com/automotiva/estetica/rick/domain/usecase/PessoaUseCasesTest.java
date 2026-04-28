package com.automotiva.estetica.rick.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import com.automotiva.estetica.rick.domain.exception.CampoInvalidoException;
import com.automotiva.estetica.rick.domain.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.PessoaGateway;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de use cases de Pessoa")
class PessoaUseCasesTest {

    @Mock
    private PessoaGateway pessoaGateway;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("CadastrarPessoaUseCase deve lançar quando CPF já existir")
    void cadastrarPessoa_deveLancarQuandoCpfDuplicado() {
        CadastrarPessoaUseCase useCase = new CadastrarPessoaUseCase(pessoaGateway, passwordEncoder);
        Pessoa pessoa = Pessoa.builder().cpf("123").email("a@x.com").senha("123").build();
        when(pessoaGateway.existePorCpf("123")).thenReturn(true);

        assertThrows(RecursoJaExisteException.class, () -> useCase.execute(pessoa, null));
        verify(pessoaGateway, never()).existePorEmail("a@x.com");
    }

    @Test
    @DisplayName("CadastrarPessoaUseCase deve lançar quando e-mail já existir")
    void cadastrarPessoa_deveLancarQuandoEmailDuplicado() {
        CadastrarPessoaUseCase useCase = new CadastrarPessoaUseCase(pessoaGateway, passwordEncoder);
        Pessoa pessoa = Pessoa.builder().cpf("123").email("a@x.com").senha("123").build();
        when(pessoaGateway.existePorCpf("123")).thenReturn(false);
        when(pessoaGateway.existePorEmail("a@x.com")).thenReturn(true);

        assertThrows(RecursoJaExisteException.class, () -> useCase.execute(pessoa, null));
    }

    @Test
    @DisplayName("CadastrarPessoaUseCase deve aplicar ROLE_CLIENTE quando rolesRequest vier vazio")
    void cadastrarPessoa_deveAplicarRolePadraoQuandoRolesVazio() {
        CadastrarPessoaUseCase useCase = new CadastrarPessoaUseCase(pessoaGateway, passwordEncoder);
        Pessoa pessoa = Pessoa.builder().cpf("123").email("a@x.com").senha("Teste@1234").build();
        when(pessoaGateway.existePorCpf("123")).thenReturn(false);
        when(pessoaGateway.existePorEmail("a@x.com")).thenReturn(false);
        when(passwordEncoder.encode("Teste@1234")).thenReturn("hash");
        when(pessoaGateway.salvar(pessoa)).thenReturn(pessoa);

        Pessoa resultado = useCase.execute(pessoa, Collections.emptySet());

        assertTrue(resultado.getRoles().contains(RoleEnum.ROLE_CLIENTE));
    }

    @Test
    @DisplayName("CadastrarPessoaUseCase deve aplicar ROLE_CLIENTE por padrão")
    void cadastrarPessoa_deveAplicarRolePadrao() {
        CadastrarPessoaUseCase useCase = new CadastrarPessoaUseCase(pessoaGateway, passwordEncoder);
        Pessoa pessoa = Pessoa.builder().cpf("123").email("a@x.com").senha("Teste@1234").build();
        when(pessoaGateway.existePorCpf("123")).thenReturn(false);
        when(pessoaGateway.existePorEmail("a@x.com")).thenReturn(false);
        when(passwordEncoder.encode("Teste@1234")).thenReturn("hash");
        when(pessoaGateway.salvar(pessoa)).thenReturn(pessoa);

        Pessoa resultado = useCase.execute(pessoa, null);

        assertEquals("hash", resultado.getSenha());
        assertTrue(resultado.getRoles().contains(RoleEnum.ROLE_CLIENTE));
        verify(pessoaGateway).salvar(pessoa);
    }

    @Test
    @DisplayName("CadastrarPessoaUseCase deve usar roles informadas")
    void cadastrarPessoa_deveUsarRolesInformadas() {
        CadastrarPessoaUseCase useCase = new CadastrarPessoaUseCase(pessoaGateway, passwordEncoder);
        Pessoa pessoa = Pessoa.builder().cpf("123").email("a@x.com").senha("Teste@1234").build();
        Set<RoleEnum> roles = EnumSet.of(RoleEnum.ROLE_GERENTE);
        when(pessoaGateway.existePorCpf("123")).thenReturn(false);
        when(pessoaGateway.existePorEmail("a@x.com")).thenReturn(false);
        when(passwordEncoder.encode("Teste@1234")).thenReturn("hash");
        when(pessoaGateway.salvar(pessoa)).thenReturn(pessoa);

        Pessoa resultado = useCase.execute(pessoa, roles);

        assertNotNull(resultado.getRoles());
        assertEquals(1, resultado.getRoles().size());
        assertTrue(resultado.getRoles().contains(RoleEnum.ROLE_GERENTE));
    }

    @Test
    @DisplayName("AtualizarPessoaUseCase deve lançar quando pessoa não existir")
    void atualizarPessoa_deveLancarQuandoNaoEncontrarPessoa() {
        AtualizarPessoaUseCase useCase = new AtualizarPessoaUseCase(pessoaGateway);
        when(pessoaGateway.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(1L, "Ana", null, null, null, null));
    }

    @Test
    @DisplayName("AtualizarPessoaUseCase deve lançar quando CPF alterado já existir")
    void atualizarPessoa_deveLancarQuandoCpfDuplicado() {
        AtualizarPessoaUseCase useCase = new AtualizarPessoaUseCase(pessoaGateway);
        Pessoa pessoa = Pessoa.builder().id(1L).cpf("111").email("a@x.com").build();
        when(pessoaGateway.buscarPorId(1L)).thenReturn(Optional.of(pessoa));
        when(pessoaGateway.existePorCpf("222")).thenReturn(true);

        assertThrows(RecursoJaExisteException.class, () -> useCase.execute(1L, null, "222", null, null, null));
    }

    @Test
    @DisplayName("AtualizarPessoaUseCase deve lançar quando e-mail alterado já existir")
    void atualizarPessoa_deveLancarQuandoEmailDuplicado() {
        AtualizarPessoaUseCase useCase = new AtualizarPessoaUseCase(pessoaGateway);
        Pessoa pessoa = Pessoa.builder().id(1L).cpf("111").email("a@x.com").build();
        when(pessoaGateway.buscarPorId(1L)).thenReturn(Optional.of(pessoa));
        when(pessoaGateway.existePorEmail("novo@x.com")).thenReturn(true);

        assertThrows(RecursoJaExisteException.class, () -> useCase.execute(1L, null, null, "novo@x.com", null, null));
    }

    @Test
    @DisplayName("AtualizarPessoaUseCase deve atualizar e salvar no caminho feliz")
    void atualizarPessoa_deveAtualizarNoCaminhoFeliz() {
        AtualizarPessoaUseCase useCase = new AtualizarPessoaUseCase(pessoaGateway);
        Pessoa pessoa = Pessoa.builder().id(1L).nome("Ana").cpf("111").email("a@x.com").build();
        when(pessoaGateway.buscarPorId(1L)).thenReturn(Optional.of(pessoa));
        when(pessoaGateway.salvar(pessoa)).thenReturn(pessoa);

        Pessoa resultado = useCase.execute(1L, "Ana Maria", "111", "a@x.com", "1199", LocalDate.of(1990, 1, 1));

        assertEquals("Ana Maria", resultado.getNome());
        verify(pessoaGateway).salvar(pessoa);
    }

    @Test
    @DisplayName("AtualizarPessoaUseCase não deve validar duplicidade quando cpf e email forem nulos")
    void atualizarPessoa_naoDeveValidarDuplicidadeQuandoCamposNulos() {
        AtualizarPessoaUseCase useCase = new AtualizarPessoaUseCase(pessoaGateway);
        Pessoa pessoa = Pessoa.builder().id(2L).nome("Carlos").cpf("111").email("c@x.com").build();
        when(pessoaGateway.buscarPorId(2L)).thenReturn(Optional.of(pessoa));
        when(pessoaGateway.salvar(pessoa)).thenReturn(pessoa);

        Pessoa resultado = useCase.execute(2L, "Carlos Silva", null, null, null, null);

        assertEquals("Carlos Silva", resultado.getNome());
        verify(pessoaGateway, never()).existePorCpf(org.mockito.ArgumentMatchers.anyString());
        verify(pessoaGateway, never()).existePorEmail(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("AtualizarPessoaUseCase não deve validar duplicidade quando cpf e email não mudarem")
    void atualizarPessoa_naoDeveValidarDuplicidadeQuandoCamposIguais() {
        AtualizarPessoaUseCase useCase = new AtualizarPessoaUseCase(pessoaGateway);
        Pessoa pessoa = Pessoa.builder().id(3L).nome("Joana").cpf("222").email("joana@x.com").build();
        when(pessoaGateway.buscarPorId(3L)).thenReturn(Optional.of(pessoa));
        when(pessoaGateway.salvar(pessoa)).thenReturn(pessoa);

        Pessoa resultado = useCase.execute(3L, "Joana", "222", "joana@x.com", "1199", null);

        assertEquals("Joana", resultado.getNome());
        verify(pessoaGateway, never()).existePorCpf("222");
        verify(pessoaGateway, never()).existePorEmail("joana@x.com");
    }

    @Test
    @DisplayName("AtualizarPessoaUseCase deve permitir cpf e email alterados quando não houver duplicidade")
    void atualizarPessoa_devePermitirAlterarCpfEEmailSemDuplicidade() {
        AtualizarPessoaUseCase useCase = new AtualizarPessoaUseCase(pessoaGateway);
        Pessoa pessoa = Pessoa.builder().id(4L).nome("Marina").cpf("333").email("m@x.com").build();
        when(pessoaGateway.buscarPorId(4L)).thenReturn(Optional.of(pessoa));
        when(pessoaGateway.existePorCpf("444")).thenReturn(false);
        when(pessoaGateway.existePorEmail("marina@x.com")).thenReturn(false);
        when(pessoaGateway.salvar(pessoa)).thenReturn(pessoa);

        Pessoa resultado = useCase.execute(4L, "Marina S", "444", "marina@x.com", "1198", LocalDate.of(1992, 5, 1));

        assertEquals("444", resultado.getCpf());
        assertEquals("marina@x.com", resultado.getEmail());
        verify(pessoaGateway).existePorCpf("444");
        verify(pessoaGateway).existePorEmail("marina@x.com");
        verify(pessoaGateway).salvar(pessoa);
    }

    @Test
    @DisplayName("AtualizarPessoaUseCase deve validar apenas cpf quando email não mudar")
    void atualizarPessoa_deveValidarSomenteCpfQuandoEmailIgual() {
        AtualizarPessoaUseCase useCase = new AtualizarPessoaUseCase(pessoaGateway);
        Pessoa pessoa = Pessoa.builder().id(5L).nome("Bruna").cpf("555").email("bruna@x.com").build();
        when(pessoaGateway.buscarPorId(5L)).thenReturn(Optional.of(pessoa));
        when(pessoaGateway.existePorCpf("556")).thenReturn(false);
        when(pessoaGateway.salvar(pessoa)).thenReturn(pessoa);

        Pessoa resultado = useCase.execute(5L, "Bruna", "556", "bruna@x.com", null, null);

        assertEquals("556", resultado.getCpf());
        verify(pessoaGateway).existePorCpf("556");
        verify(pessoaGateway, never()).existePorEmail("bruna@x.com");
    }

    @Test
    @DisplayName("AtualizarSenhaPessoaUseCase deve lançar quando pessoa não existir")
    void atualizarSenha_deveLancarQuandoPessoaNaoExiste() {
        AtualizarSenhaPessoaUseCase useCase = new AtualizarSenhaPessoaUseCase(pessoaGateway, passwordEncoder);
        when(pessoaGateway.buscarPorId(10L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> useCase.execute(10L, "123", "456"));
    }

    @Test
    @DisplayName("AtualizarSenhaPessoaUseCase deve lançar com dados inválidos")
    void atualizarSenha_deveLancarQuandoDadosInvalidos() {
        AtualizarSenhaPessoaUseCase useCase = new AtualizarSenhaPessoaUseCase(pessoaGateway, passwordEncoder);
        Pessoa pessoa = Pessoa.builder().id(1L).senha("hash").build();
        when(pessoaGateway.buscarPorId(1L)).thenReturn(Optional.of(pessoa));

        assertThrows(CampoInvalidoException.class, () -> useCase.execute(1L, " ", "nova"));
    }

    @Test
    @DisplayName("AtualizarSenhaPessoaUseCase deve lançar quando senha atual não confere")
    void atualizarSenha_deveLancarQuandoSenhaAtualNaoConfere() {
        AtualizarSenhaPessoaUseCase useCase = new AtualizarSenhaPessoaUseCase(pessoaGateway, passwordEncoder);
        Pessoa pessoa = Pessoa.builder().id(1L).senha("hash").build();
        when(pessoaGateway.buscarPorId(1L)).thenReturn(Optional.of(pessoa));

        assertThrows(CampoInvalidoException.class, () -> useCase.execute(1L, "123", "456"));
        verify(pessoaGateway, never()).salvar(pessoa);
    }

    @Test
    @DisplayName("AtualizarSenhaPessoaUseCase deve salvar nova senha no caminho feliz")
    void atualizarSenha_deveSalvarNovaSenha() {
        AtualizarSenhaPessoaUseCase useCase = new AtualizarSenhaPessoaUseCase(pessoaGateway, passwordEncoder);
        Pessoa pessoa = Pessoa.builder().id(1L).senha("hash").build();
        when(pessoaGateway.buscarPorId(1L)).thenReturn(Optional.of(pessoa));
        when(passwordEncoder.matches("Teste@1234", "hash")).thenReturn(true);
        when(passwordEncoder.encode("Teste@4321")).thenReturn("hash-novo");

        useCase.execute(1L, "Teste@1234", "Teste@4321");

        assertEquals("hash-novo", pessoa.getSenha());
        verify(pessoaGateway).salvar(pessoa);
    }

    @Test
    @DisplayName("LoginPessoaUseCase deve retornar pessoa quando email existir")
    void loginPessoa_deveRetornarPessoaQuandoExistir() {
        LoginPessoaUseCase useCase = new LoginPessoaUseCase(pessoaGateway);
        Pessoa pessoa = Pessoa.builder().id(1L).email("ana@x.com").build();
        when(pessoaGateway.buscarPorEmail("ana@x.com")).thenReturn(Optional.of(pessoa));

        Pessoa resultado = useCase.execute("ana@x.com");

        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("LoginPessoaUseCase deve lançar UsernameNotFoundException quando email não existir")
    void loginPessoa_deveLancarQuandoEmailNaoExistir() {
        LoginPessoaUseCase useCase = new LoginPessoaUseCase(pessoaGateway);
        when(pessoaGateway.buscarPorEmail("inexistente@x.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> useCase.execute("inexistente@x.com"));
    }
}
