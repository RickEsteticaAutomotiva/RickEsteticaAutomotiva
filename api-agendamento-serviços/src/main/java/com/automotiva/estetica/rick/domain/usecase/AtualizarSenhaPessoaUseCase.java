package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.exception.CampoInvalidoException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.PessoaGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Use Case para atualizar a senha de uma Pessoa.
 */
@Service
@RequiredArgsConstructor
public class AtualizarSenhaPessoaUseCase {

    private final PessoaGateway pessoaGateway;
    private final PasswordEncoder passwordEncoder;

    public void execute(Long id, String senhaAtual, String novaSenha) {
        Pessoa pessoa = pessoaGateway.buscarPorId(id).orElseThrow(() -> RecursoNaoEncontradoException.builder()
                .mensagem("a pessoa com id " + id + " não foi encontrada").detalhes("").build());

        // Validação de força da senha (regra de domínio - valida nulo/branco/força)
        pessoa.validaSenha(novaSenha);

        // Validar que senha atual está correta
        if (!passwordEncoder.matches(senhaAtual, pessoa.getSenha())) {
            throw CampoInvalidoException.builder().mensagem("dados de senha inválidos").detalhes("").build();
        }

        pessoa.setSenha(passwordEncoder.encode(novaSenha));
        pessoaGateway.salvar(pessoa);
    }
}
