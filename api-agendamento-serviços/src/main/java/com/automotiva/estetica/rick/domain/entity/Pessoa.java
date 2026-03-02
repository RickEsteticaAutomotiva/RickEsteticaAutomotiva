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
     * Conjunto de roles do usuário.
     * Default: {@code ROLE_USER} quando vazio ou nulo.
     */
    @Builder.Default
    private Set<RoleEnum> roles = EnumSet.of(RoleEnum.ROLE_CLIENTE);

    /**
     * Retorna {@code true} se o usuário possuir a role informada.
     */
    public boolean temRole(RoleEnum role) {
        return roles != null && roles.contains(role);
    }

    public void atualizar(String nome, String cpf, String email, String telefone, LocalDate dataNascimento) {
        if (nome != null) this.nome = nome;
        if (cpf != null) this.cpf = cpf;
        if (email != null) this.email = email;
        if (telefone != null) this.telefone = telefone;
        if (dataNascimento != null) this.dataNascimento = dataNascimento;
    }

    /**
     * Regra de domínio: valida que os campos de troca de senha não são nulos nem em branco.
     * Deve ser chamado antes de codificar a nova senha na camada de aplicação.
     */
    public void validarDadosSenha(String senhaAtual, String novaSenha) {
        if (senhaAtual == null || senhaAtual.isBlank() || novaSenha == null || novaSenha.isBlank()) {
            throw CampoInvalidoException.builder()
                    .mensagem("dados de senha inválidos")
                    .detalhes("")
                    .build();
        }
    }

    public void alterarSenha(String senhaNova) {
        this.senha = senhaNova;
    }
}
