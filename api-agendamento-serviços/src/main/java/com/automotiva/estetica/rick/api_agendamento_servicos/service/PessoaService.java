package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.entity.PessoaEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoComListaObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

}
