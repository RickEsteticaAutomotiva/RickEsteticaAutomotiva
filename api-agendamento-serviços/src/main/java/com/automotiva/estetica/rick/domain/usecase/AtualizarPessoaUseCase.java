package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.PessoaGateway;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Use Case para atualizar uma Pessoa existente.
 */
@Service
@RequiredArgsConstructor
public class AtualizarPessoaUseCase {

    private final PessoaGateway pessoaGateway;

    public Pessoa execute(Long id, String nome, String cpf, String email, String telefone, LocalDate dataNascimento) {
        Pessoa pessoa = pessoaGateway.buscarPorId(id).orElseThrow(() -> RecursoNaoEncontradoException.builder()
                .mensagem("a pessoa com id " + id + " não foi encontrada").detalhes("").build());

        // Validar CPF duplicado (se alterado)
        if (cpf != null && !cpf.equals(pessoa.getCpf()) && pessoaGateway.existePorCpf(cpf)) {
            throw RecursoJaExisteException.builder().mensagem("o cpf já existe no sistema").detalhes("").build();
        }

        // Validar Email duplicado (se alterado)
        if (email != null && !email.equals(pessoa.getEmail()) && pessoaGateway.existePorEmail(email)) {
            throw RecursoJaExisteException.builder().mensagem("o email já existe no sistema").detalhes("").build();
        }

        pessoa.atualizar(nome, cpf, email, telefone, dataNascimento);
        return pessoaGateway.salvar(pessoa);
    }
}
