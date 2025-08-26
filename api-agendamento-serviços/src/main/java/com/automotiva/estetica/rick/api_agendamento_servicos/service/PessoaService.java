package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.LoginDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.PessoaCadastroDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.PessoaDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.PessoaEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoComListaObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoComObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoSemObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PessoaService {
    @Autowired
    PessoaRepository pessoaRepository;

//    public RetornoComListaObjeto<PessoaEntity>buscarTodos() {
//
//        var retornoComListaObjeto = new RetornoComListaObjeto<PessoaEntity>();
//
//        try {
//            List<PessoaEntity> pessoas = pessoaRepository.findAll();
//
//            if (pessoas.isEmpty()) {
//
//                retornoComListaObjeto.setStatusCode(404);
//                retornoComListaObjeto.setMensagem("Nenhuma pessoa encontrada.");
//                retornoComListaObjeto.setObjeto(List.of()); // lista vazia
//            } else {
//                retornoComListaObjeto.setStatusCode(200);
//                retornoComListaObjeto.setMensagem("Pessoas encontradas com sucesso.");
//                retornoComListaObjeto.setObjeto(pessoas);
//            }
//        } catch (Exception e) {
//            retornoComListaObjeto.setStatusCode(500);
//            retornoComListaObjeto.setMensagem("Erro ao buscar pessoas: " + e.getMessage());
//            retornoComListaObjeto.setObjeto(List.of());
//        }
//
//        return retornoComListaObjeto;
//    }

    public RetornoComListaObjeto<PessoaEntity> buscarTodos() {

        try {
            List<PessoaEntity> pessoas = pessoaRepository.findAll();

            if (pessoas.isEmpty()) {
                return RetornoComListaObjeto.<PessoaEntity>builder()
                        .statusCode(404)
                        .mensagem("Nenhuma pessoa encontrada.")
                        .objeto(List.of())
                        .build();
            }

            return RetornoComListaObjeto.<PessoaEntity>builder()
                    .statusCode(200)
                    .mensagem("Pessoas encontradas com sucesso.")
                    .objeto(pessoas)
                    .build();

        } catch (Exception e) {
            return RetornoComListaObjeto.<PessoaEntity>builder()
                    .statusCode(500)
                    .mensagem("Erro ao buscar pessoas: " + e.getMessage())
                    .objeto(List.of())
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

    public RetornoSemObjeto cadastro(PessoaCadastroDto pessoaDto) {
        var retorno = new RetornoSemObjeto();

        try {
            var novaPessoa = PessoaEntity.builder()
//                    .nome(pessoaDto.getNome())
                    .email(pessoaDto.getEmail())
                    .senha(pessoaDto.getSenha())
                    .build();

            pessoaRepository.save(novaPessoa);

            retorno.setStatusCode(201);
            retorno.setMensagem("Pessoa cadastrada com sucesso.");
        } catch (Exception e) {
            retorno.setStatusCode(500);
            retorno.setMensagem("Erro ao cadastrar pessoa: " + e.getMessage());
        }

        return retorno;
    }
}
