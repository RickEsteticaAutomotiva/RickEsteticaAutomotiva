package com.automotiva.estetica.rick.domain.entity;

import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import com.automotiva.estetica.rick.domain.exception.CampoInvalidoException;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pessoa {

    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    private String senha;

    /**
     * Conjunto de roles do usuário. Default: {@code ROLE_USER} quando vazio ou
     * nulo.
     */
    @Builder.Default
    private Set<RoleEnum> roles = EnumSet.of(RoleEnum.ROLE_CLIENTE);

    /** Retorna {@code true} se o usuário possuir a role informada. */
    public boolean temRole(RoleEnum role) {
        return roles != null && roles.contains(role);
    }

    public void atualizar(String nome, String cpf, String email, String telefone, LocalDate dataNascimento) {
        if (nome != null)
            this.nome = nome;
        if (cpf != null)
            this.cpf = cpf;
        if (email != null)
            this.email = email;
        if (telefone != null)
            this.telefone = telefone;
        if (dataNascimento != null)
            this.dataNascimento = dataNascimento;
    }

    /**
     * Valida a força da senha de acordo com critérios de segurança. Também valida
     * que a senha não é nula nem em branco. Requisitos: - Não nula nem em branco -
     * Mínimo de 8 caracteres - Pelo menos 1 letra maiúscula - Pelo menos 1 letra
     * minúscula - Pelo menos 1 número - Pelo menos 1 caractere especial
     * (!@#$%^&*()_+-=[]{}|;':"<>,.?/)
     *
     * @param senha
     *            A senha a ser validada
     * @throws CampoInvalidoException
     *             se a senha não atender aos critérios de força
     */
    public void validaSenha(String senha) {
        // Validação de nulo ou em branco
        if (senha == null || senha.isBlank()) {
            throw CampoInvalidoException.builder().mensagem("senha não pode ser nula ou vazia").detalhes("").build();
        }

        StringBuilder erros = new StringBuilder();

        // Validar comprimento mínimo
        if (senha.length() < 8) {
            erros.append("Mínimo de 8 caracteres; ");
        }

        // Validar presença de letra maiúscula
        if (!senha.matches(".*[A-Z].*")) {
            erros.append("Pelo menos 1 letra maiúscula; ");
        }

        // Validar presença de letra minúscula
        if (!senha.matches(".*[a-z].*")) {
            erros.append("Pelo menos 1 letra minúscula; ");
        }

        // Validar presença de número
        if (!senha.matches(".*[0-9].*")) {
            erros.append("Pelo menos 1 número; ");
        }

        // Validar presença de caractere especial
        if (!senha.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;':\"<>,.?/].*")) {
            erros.append("Pelo menos 1 caractere especial (!@#$%^&*()_+-=[]{}|;':\"<>,.?/)");
        }

        // Se houver erros, lançar exceção
        if (erros.length() > 0) {
            String mensagemErro = "Senha fraca. Requisitos: " + erros.toString().trim();
            if (mensagemErro.endsWith(";")) {
                mensagemErro = mensagemErro.substring(0, mensagemErro.length() - 1);
            }
            throw CampoInvalidoException.builder().mensagem(mensagemErro).detalhes("").build();
        }
    }
}
