package com.automotiva.estetica.rick.infrastructure.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.infrastructure.entity.PessoaEntity;
import com.automotiva.estetica.rick.infrastructure.entity.RoleEntity;
import com.automotiva.estetica.rick.infrastructure.mapper.PessoaEntityMapper;
import com.automotiva.estetica.rick.infrastructure.repository.pessoa.PessoaRepository;
import com.automotiva.estetica.rick.infrastructure.repository.pessoa.RoleRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de PessoaGatewayImpl")
class PessoaGatewayImplTest {

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PessoaEntityMapper pessoaEntityMapper;

    @InjectMocks
    private PessoaGatewayImpl gateway;

    @Test
    @DisplayName("salvar deve aplicar role padrao ROLE_CLIENTE quando roles vier nulo")
    void salvar_quandoRolesNulo_deveAplicarRolePadrao() {
        Pessoa pessoa = Pessoa.builder().id(1L).nome("Joao").roles(null).build();
        PessoaEntity entity = PessoaEntity.builder().id(1L).build();
        RoleEntity roleCliente = RoleEntity.builder().nome(RoleEnum.ROLE_CLIENTE).build();

        when(pessoaEntityMapper.toEntity(pessoa)).thenReturn(entity);
        when(roleRepository.findByNome(RoleEnum.ROLE_CLIENTE)).thenReturn(Optional.of(roleCliente));
        when(pessoaRepository.save(any(PessoaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pessoaEntityMapper.toDomain(any(PessoaEntity.class))).thenReturn(Pessoa.builder().id(1L).build());

        Pessoa resultado = gateway.salvar(pessoa);

        ArgumentCaptor<PessoaEntity> captor = ArgumentCaptor.forClass(PessoaEntity.class);
        verify(pessoaRepository).save(captor.capture());
        assertNotNull(captor.getValue().getRoles());
        assertEquals(1, captor.getValue().getRoles().size());
        assertEquals(RoleEnum.ROLE_CLIENTE, captor.getValue().getRoles().iterator().next().getNome());
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("salvar deve aplicar role padrao ROLE_CLIENTE quando roles vier vazio")
    void salvar_quandoRolesVazio_deveAplicarRolePadrao() {
        Pessoa pessoa = Pessoa.builder().id(3L).nome("Maria").roles(Collections.emptySet()).build();
        PessoaEntity entity = PessoaEntity.builder().id(3L).build();
        RoleEntity roleCliente = RoleEntity.builder().nome(RoleEnum.ROLE_CLIENTE).build();

        when(pessoaEntityMapper.toEntity(pessoa)).thenReturn(entity);
        when(roleRepository.findByNome(RoleEnum.ROLE_CLIENTE)).thenReturn(Optional.of(roleCliente));
        when(pessoaRepository.save(any(PessoaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pessoaEntityMapper.toDomain(any(PessoaEntity.class))).thenReturn(Pessoa.builder().id(3L).build());

        gateway.salvar(pessoa);

        ArgumentCaptor<PessoaEntity> captor = ArgumentCaptor.forClass(PessoaEntity.class);
        verify(pessoaRepository).save(captor.capture());
        assertEquals(1, captor.getValue().getRoles().size());
        assertEquals(RoleEnum.ROLE_CLIENTE, captor.getValue().getRoles().iterator().next().getNome());
    }

    @Test
    @DisplayName("salvar deve criar role quando enum nao existir no banco")
    void salvar_quandoRoleNaoExistir_deveCriarRole() {
        Pessoa pessoa = Pessoa.builder().id(2L).roles(EnumSet.of(RoleEnum.ROLE_GERENTE)).build();
        PessoaEntity entity = PessoaEntity.builder().id(2L).build();
        RoleEntity roleCriada = RoleEntity.builder().nome(RoleEnum.ROLE_GERENTE).build();

        when(pessoaEntityMapper.toEntity(pessoa)).thenReturn(entity);
        when(roleRepository.findByNome(RoleEnum.ROLE_GERENTE)).thenReturn(Optional.empty());
        when(roleRepository.save(any(RoleEntity.class))).thenReturn(roleCriada);
        when(pessoaRepository.save(any(PessoaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pessoaEntityMapper.toDomain(any(PessoaEntity.class))).thenReturn(Pessoa.builder().id(2L).build());

        gateway.salvar(pessoa);

        verify(roleRepository).save(any(RoleEntity.class));
        ArgumentCaptor<PessoaEntity> captor = ArgumentCaptor.forClass(PessoaEntity.class);
        verify(pessoaRepository).save(captor.capture());
        Set<RoleEntity> rolesPersistidas = captor.getValue().getRoles();
        assertEquals(1, rolesPersistidas.size());
        assertEquals(RoleEnum.ROLE_GERENTE, rolesPersistidas.iterator().next().getNome());
    }

    @Test
    @DisplayName("deletarPorId deve marcar data de exclusao logica")
    void deletarPorId_quandoPessoaExiste_deveAplicarSoftDelete() {
        PessoaEntity entity = PessoaEntity.builder().id(10L).build();
        when(pessoaRepository.findById(10L)).thenReturn(Optional.of(entity));

        gateway.deletarPorId(10L);

        ArgumentCaptor<PessoaEntity> captor = ArgumentCaptor.forClass(PessoaEntity.class);
        verify(pessoaRepository).save(captor.capture());
        assertNotNull(captor.getValue().getDeletadoEm());
    }

    @Test
    @DisplayName("deletarPorId deve lancar excecao quando pessoa nao existir")
    void deletarPorId_quandoNaoExiste_deveLancarRecursoNaoEncontrado() {
        when(pessoaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> gateway.deletarPorId(99L));
    }

    @Test
    @DisplayName("buscarPorId deve mapear optional")
    void buscarPorId_deveMapearOptional() {
        PessoaEntity entity = PessoaEntity.builder().id(50L).deletadoEm(LocalDateTime.now()).build();
        Pessoa pessoa = Pessoa.builder().id(50L).build();
        when(pessoaRepository.findById(50L)).thenReturn(Optional.of(entity));
        when(pessoaEntityMapper.toDomain(entity)).thenReturn(pessoa);

        var resultado = gateway.buscarPorId(50L);

        assertTrue(resultado.isPresent());
        assertEquals(50L, resultado.orElseThrow().getId());
    }

    @Test
    @DisplayName("buscarPorEmail deve mapear optional")
    void buscarPorEmail_deveMapearOptional() {
        PessoaEntity entity = PessoaEntity.builder().id(60L).build();
        Pessoa pessoa = Pessoa.builder().id(60L).email("maria@x.com").build();
        when(pessoaRepository.findByEmail("maria@x.com")).thenReturn(Optional.of(entity));
        when(pessoaEntityMapper.toDomain(entity)).thenReturn(pessoa);

        var resultado = gateway.buscarPorEmail("maria@x.com");

        assertTrue(resultado.isPresent());
        assertEquals(60L, resultado.orElseThrow().getId());
    }

    @Test
    @DisplayName("buscarTodos deve delegar especificacao e mapear pagina")
    void buscarTodos_deveDelegarEMapear() {
        var pageable = PageRequest.of(0, 10);
        PessoaEntity entity = PessoaEntity.builder().id(70L).build();
        Pessoa pessoa = Pessoa.builder().id(70L).build();
        when(pessoaRepository.findAll(org.mockito.ArgumentMatchers.<Specification<PessoaEntity>>any(),
                any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(java.util.List.of(entity), pageable, 1));
        when(pessoaEntityMapper.toDomain(entity)).thenReturn(pessoa);

        var resultado = gateway.buscarTodos("ana", pageable);

        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    @DisplayName("existePorCpf, existePorEmail e existePorId devem delegar ao repositorio")
    void exists_deveDelegarAoRepositorio() {
        when(pessoaRepository.existsByCpf("123")).thenReturn(true);
        when(pessoaRepository.existsByEmail("mail@x.com")).thenReturn(false);
        when(pessoaRepository.existsById(88L)).thenReturn(true);

        assertTrue(gateway.existePorCpf("123"));
        assertFalse(gateway.existePorEmail("mail@x.com"));
        assertTrue(gateway.existePorId(88L));
    }
}






