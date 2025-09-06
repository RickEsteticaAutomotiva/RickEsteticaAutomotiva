package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.LoginDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.PessoaCadastroDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.PessoaDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.PessoaEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoComListaObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoComObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoComPaginacao;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoSemObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PessoaService {
    @Autowired
    PessoaRepository pessoaRepository;

    public RetornoComPaginacao<PessoaDto> buscarTodos(Pageable pageable) {
        try {
            Page<PessoaEntity> paginaPessoas = pessoaRepository.findAll(pageable);

            if (paginaPessoas.getContent().isEmpty()) {
                return RetornoComPaginacao.<PessoaDto>builder()
                        .statusCode(404)
                        .mensagem("Nenhuma pessoa encontrada.")
                        .conteudo(List.of())
                        .paginaAtual(pageable.getPageNumber())
                        .totalPaginas(0)
                        .totalElementos(0)
                        .tamanhoPagina(pageable.getPageSize())
                        .ultimaPagina(true)
                        .primeiraPagina(true)
                        .build();
            }

            List<PessoaDto> pessoasDto = converterListaParaDto(paginaPessoas.getContent());

            return RetornoComPaginacao.<PessoaDto>builder()
                    .statusCode(200)
                    .mensagem("Pessoas encontradas com sucesso.")
                    .conteudo(pessoasDto)
                    .paginaAtual(paginaPessoas.getNumber())
                    .totalPaginas(paginaPessoas.getTotalPages())
                    .totalElementos(paginaPessoas.getTotalElements())
                    .tamanhoPagina(paginaPessoas.getSize())
                    .ultimaPagina(paginaPessoas.isLast())
                    .primeiraPagina(paginaPessoas.isFirst())
                    .build();

        } catch (Exception e) {
            return RetornoComPaginacao.<PessoaDto>builder()
                    .statusCode(500)
                    .mensagem("Erro ao buscar pessoas: " + e.getMessage())
                    .conteudo(List.of())
                    .paginaAtual(0)
                    .totalPaginas(0)
                    .totalElementos(0)
                    .tamanhoPagina(0)
                    .ultimaPagina(true)
                    .primeiraPagina(true)
                    .build();
        }
    }

    public RetornoComObjeto<PessoaDto> login(LoginDto loginDto){
        var retorno = new RetornoComObjeto<PessoaDto>();

        try {
            Optional<PessoaEntity> autenticado = pessoaRepository.findByEmailAndSenha(loginDto.getEmail(), loginDto.getSenha());

            if (autenticado.isPresent()) {

                var pessoaDto = PessoaDto.builder()
                        .id(autenticado.get().getId())
                        .nome(autenticado.get().getNome())
                        .email(autenticado.get().getEmail())
                        .dataNascimento(autenticado.get().getDataNascimento())
                        .build();

                retorno.setStatusCode(200);
                retorno.setObjeto(pessoaDto);
            } else {
                retorno.setStatusCode(401);
                retorno.setMensagem("Credenciais inválidas.");
            }
        } catch (Exception e) {
            retorno.setStatusCode(500);
            retorno.setMensagem("Erro ao processar login: " + e.getMessage());
        }

        return retorno;
    }

    public RetornoSemObjeto criarPessoa(PessoaCadastroDto pessoa) {
        try {

            if (pessoaRepository.existsByCpf(pessoa.getCpf())) {
                return RetornoSemObjeto.builder()
                        .statusCode(400)
                        .mensagem("CPF já cadastrado.")
                        .build();
            }

            if (pessoaRepository.existsByEmail(pessoa.getEmail())) {
                return RetornoSemObjeto.builder()
                        .statusCode(400)
                        .mensagem("Email já cadastrado.")
                        .build();
            }

            var pessoaEntity = converterEntity(pessoa);
            pessoaEntity.setSenha(pessoa.getSenha());
            pessoaRepository.save(pessoaEntity);

            return RetornoSemObjeto.builder()
                    .statusCode(201)
                    .mensagem("Pessoa criada com sucesso.")
                    .build();

        } catch (Exception e) {
            return RetornoSemObjeto.builder()
                    .statusCode(500)
                    .mensagem("Erro ao criar pessoa: " + e.getMessage())
                    .build();
        }
    }

    public RetornoComObjeto<PessoaDto> buscarPorId(Long id) {
        try {
            Optional<PessoaEntity> pessoa = pessoaRepository.findById(id);

            if (pessoa.isEmpty()) {
                return RetornoComObjeto.<PessoaDto>builder()
                        .statusCode(404)
                        .mensagem("Pessoa não encontrada.")
                        .objeto(null)
                        .build();
            }

            return RetornoComObjeto.<PessoaDto>builder()
                    .statusCode(200)
                    .mensagem("Pessoa encontrada com sucesso.")
                    .objeto(converterParaDto(pessoa.get()))
                    .build();

        } catch (Exception e) {
            return RetornoComObjeto.<PessoaDto>builder()
                    .statusCode(500)
                    .mensagem("Erro ao buscar pessoa: " + e.getMessage())
                    .objeto(null)
                    .build();
        }
    }

    public RetornoComObjeto<PessoaCadastroDto> atualizarPessoa(Long id, PessoaCadastroDto pessoaAtualizada) {
        try {
            Optional<PessoaEntity> pessoaExistente = pessoaRepository.findById(id);

            if (pessoaExistente.isEmpty()) {
                return RetornoComObjeto.<PessoaCadastroDto>builder()
                        .statusCode(404)
                        .mensagem("Pessoa não encontrada.")
                        .objeto(null)
                        .build();
            }

            PessoaEntity pessoa = pessoaExistente.get();

            if (!pessoa.getCpf().equals(pessoaAtualizada.getCpf()) &&
                    pessoaRepository.existsByCpf(pessoaAtualizada.getCpf())) {
                return RetornoComObjeto.<PessoaCadastroDto>builder()
                        .statusCode(400)
                        .mensagem("CPF já cadastrado para outra pessoa.")
                        .objeto(null)
                        .build();
            }


            if (!pessoa.getEmail().equals(pessoaAtualizada.getEmail()) &&
                    pessoaRepository.existsByEmail(pessoaAtualizada.getEmail())) {
                return RetornoComObjeto.<PessoaCadastroDto>builder()
                        .statusCode(400)
                        .mensagem("Email já cadastrado para outra pessoa.")
                        .objeto(null)
                        .build();
            }


            atualizarPessoaEntiry(pessoaAtualizada, pessoa);
            pessoa.setSenha(pessoaAtualizada.getSenha());

            pessoaRepository.save(pessoa);

            return RetornoComObjeto.<PessoaCadastroDto>builder()
                    .statusCode(200)
                    .mensagem("Pessoa atualizada com sucesso.")
                    .objeto(pessoaAtualizada)
                    .build();

        } catch (Exception e) {
            return RetornoComObjeto.<PessoaCadastroDto>builder()
                    .statusCode(500)
                    .mensagem("Erro ao atualizar pessoa: " + e.getMessage())
                    .objeto(null)
                    .build();
        }
    }

    public RetornoSemObjeto deletarPessoa(Long id) {
        try {
            Optional<PessoaEntity> pessoa = pessoaRepository.findById(id);

            if (pessoa.isEmpty()) {
                return RetornoSemObjeto.builder()
                        .statusCode(404)
                        .mensagem("Pessoa não encontrada.")
                        .build();
            }

            pessoaRepository.deleteById(id);

            return RetornoSemObjeto.builder()
                    .statusCode(200)
                    .mensagem("Pessoa deletada com sucesso.")
                    .build();

        } catch (Exception e) {
            return RetornoSemObjeto.builder()
                    .statusCode(500)
                    .mensagem("Erro ao deletar pessoa: " + e.getMessage())
                    .build();
        }
    }

    private PessoaDto converterParaDto(PessoaEntity entity) {
        return PessoaDto.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .cpf(entity.getCpf())
                .email(entity.getEmail())
                .telefone(entity.getTelefone())
                .dataNascimento(entity.getDataNascimento())
                .build();
    }

    private List<PessoaDto> converterListaParaDto(List<PessoaEntity> entities) {
        return entities.stream()
                .map(this::converterParaDto)
                .collect(Collectors.toList());
    }

    public PessoaEntity converterEntity(PessoaCadastroDto dto) {
        return PessoaEntity.builder()
                .nome(dto.getNome())
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .telefone(dto.getTelefone())
                .dataNascimento(dto.getDataNascimento())
                .build();
    }

    public void atualizarPessoaEntiry(PessoaCadastroDto dto, PessoaEntity entity) {
        entity.setNome(dto.getNome());
        entity.setCpf(dto.getCpf());
        entity.setEmail(dto.getEmail());
        entity.setTelefone(dto.getTelefone());
        entity.setDataNascimento(dto.getDataNascimento());
    }
}
