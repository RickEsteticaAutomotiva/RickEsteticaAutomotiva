package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import com.automotiva.estetica.rick.domain.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.domain.gateway.PessoaGateway;
import java.util.EnumSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Use Case para cadastrar uma nova Pessoa. Encapsula a lógica de negócio de
 * criação.
 */
@Service
@RequiredArgsConstructor
public class CadastrarPessoaUseCase {

    private final PessoaGateway pessoaGateway;
    private final PasswordEncoder passwordEncoder;

    public Pessoa execute(Pessoa pessoa, Set<RoleEnum> rolesRequest) {
        // Validação de duplicação
        if (pessoaGateway.existePorCpf(pessoa.getCpf())) {
            throw RecursoJaExisteException.builder().mensagem("o cpf já existe no sistema").detalhes("").build();
        }
        if (pessoaGateway.existePorEmail(pessoa.getEmail())) {
            throw RecursoJaExisteException.builder().mensagem("o email já existe no sistema").detalhes("").build();
        }

        // Roles: usa as informadas no request ou ROLE_CLIENTE por padrão
        Set<RoleEnum> roles = (rolesRequest != null && !rolesRequest.isEmpty())
                ? EnumSet.copyOf(rolesRequest)
                : EnumSet.of(RoleEnum.ROLE_CLIENTE);

        // Codificar senha
        pessoa.setSenha(passwordEncoder.encode(pessoa.getSenha()));
        pessoa.setRoles(roles);

        return pessoaGateway.salvar(pessoa);
    }
}
