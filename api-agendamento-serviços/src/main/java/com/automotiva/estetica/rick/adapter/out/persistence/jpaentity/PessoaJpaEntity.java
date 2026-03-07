package com.automotiva.estetica.rick.adapter.out.persistence.jpaentity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pessoa")
@SQLRestriction("deletado_em IS NULL")
public class PessoaJpaEntity extends BaseJpaEntity<Long> {

    @Column(name = "nome", length = 100)
    private String nome;

    @Column(name = "cpf", length = 11, unique = true)
    private String cpf;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "senha")
    private String senha;

    /**
     * Data e hora em que a pessoa foi inativada (soft delete). {@code null} indica que o registro
     * está ativo.
     */
    @Column(name = "deletado_em")
    private LocalDateTime deletadoEm;

    /**
     * Conjunto de roles do usuário persistido na tabela de junção {@code pessoa_roles}.
     *
     * <p>EAGER é aceitável aqui pois roles são pequenas e sempre necessárias no carregamento do
     * usuário para autenticação (Spring Security).
     *
     * <p>Sem CascadeType: roles são entidades de referência independentes — nunca devem ser
     * criadas/removidas em cascata a partir de Pessoa. Fonte: Spring Security Reference —
     * GrantedAuthority; Hibernate ORM Guide — @ManyToMany.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "pessoa_roles",
            joinColumns = @JoinColumn(name = "pessoa_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleJpaEntity> roles = new HashSet<>();
}
