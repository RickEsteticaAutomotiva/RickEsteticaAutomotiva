package com.automotiva.estetica.rick.application.dto.request;

import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
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
public class PessoaCadastroRequest {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório")
    private String cpf;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    private String telefone;

    private LocalDate dataNascimento;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;

    /**
     * Roles do usuário. Opcional: quando ausente o serviço atribui
     * {@code ROLE_USER} por padrão. Somente ADMIN pode cadastrar usuários com roles
     * elevadas (validação no service).
     */
    private Set<RoleEnum> roles;
}
