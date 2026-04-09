package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.gateway.PessoaGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Use Case para buscar Pessoa por email para login. A validação de senha é
 * responsabilidade da Application Layer.
 */
@Service
@RequiredArgsConstructor
public class LoginPessoaUseCase {

    private final PessoaGateway pessoaGateway;

    /**
     * Busca uma pessoa pelo email.
     *
     * @param email
     *            Email da pessoa
     * @return Pessoa encontrada
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException
     *             se não encontrada
     */
    public Pessoa execute(String email) {
        return pessoaGateway.buscarPorEmail(email)
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
                        "Usuário não encontrado: " + email));
    }
}
