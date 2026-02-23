package com.automotiva.estetica.rick.application.port.in;

import com.automotiva.estetica.rick.application.dto.request.LoginRequest;
import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.request.PessoaAtualizacaoRequest;
import com.automotiva.estetica.rick.application.dto.request.PessoaCadastroRequest;
import com.automotiva.estetica.rick.application.dto.request.SenhaRequest;
import com.automotiva.estetica.rick.application.dto.response.PessoaResponse;
import com.automotiva.estetica.rick.application.dto.response.TokenResponse;
import org.springframework.data.domain.Page;

public interface PessoaUseCase {

    Page<PessoaResponse> buscarTodos(PageRequest pageRequest);

    PessoaResponse buscarPorId(Long id);

    PessoaResponse cadastrar(PessoaCadastroRequest request);

    PessoaResponse atualizar(Long id, PessoaAtualizacaoRequest request);

    void deletar(Long id);

    TokenResponse login(LoginRequest request);

    void atualizarSenha(Long id, SenhaRequest request);
}
