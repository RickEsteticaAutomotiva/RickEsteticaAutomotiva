package com.automotiva.estetica.rick.application.mapper;

import com.automotiva.estetica.rick.application.dto.request.ChamadaFuncaoRequestDTO;
import com.automotiva.estetica.rick.application.dto.request.MensagemAssistenteDTO;
import com.automotiva.estetica.rick.application.dto.request.ParteMensagemDTO;
import com.automotiva.estetica.rick.application.dto.request.RespostaFuncaoDTO;
import com.automotiva.estetica.rick.application.dto.response.AssistenteMensagemResponse;
import com.automotiva.estetica.rick.application.dto.response.ChamadaFuncaoResponseDTO;
import com.automotiva.estetica.rick.domain.entity.assistente.ChamadaFuncao;
import com.automotiva.estetica.rick.domain.entity.assistente.MensagemAssistente;
import com.automotiva.estetica.rick.domain.entity.assistente.PapelMensagem;
import com.automotiva.estetica.rick.domain.entity.assistente.ParteMensagem;
import com.automotiva.estetica.rick.domain.entity.assistente.RespostaAssistente;
import com.automotiva.estetica.rick.domain.entity.assistente.RespostaFuncao;
import com.automotiva.estetica.rick.domain.exception.CampoInvalidoException;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Mapper manual (não MapStruct) por causa dos campos {@code Map<String, Object>}
 * de argumentos/resposta de função, que o MapStruct não mapeia de forma direta.
 */
@Component
public class AssistenteDTOMapper {

    public List<MensagemAssistente> toDomain(List<MensagemAssistenteDTO> historico) {
        return historico.stream().map(this::toDomainMensagem).toList();
    }

    private MensagemAssistente toDomainMensagem(MensagemAssistenteDTO dto) {
        return MensagemAssistente.builder().papel(toPapel(dto.getPapel()))
                .partes(dto.getPartes().stream().map(this::toDomainParte).toList()).build();
    }

    private PapelMensagem toPapel(String papel) {
        if ("usuario".equalsIgnoreCase(papel)) {
            return PapelMensagem.USUARIO;
        }
        if ("modelo".equalsIgnoreCase(papel)) {
            return PapelMensagem.MODELO;
        }
        throw CampoInvalidoException.builder().mensagem("Papel de mensagem inválido: " + papel).build();
    }

    private ParteMensagem toDomainParte(ParteMensagemDTO dto) {
        return ParteMensagem.builder().texto(dto.getTexto()).chamadaFuncao(toDomainChamada(dto.getChamadaFuncao()))
                .respostaFuncao(toDomainResposta(dto.getRespostaFuncao())).build();
    }

    private ChamadaFuncao toDomainChamada(ChamadaFuncaoRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return ChamadaFuncao.builder().nome(dto.getNome()).argumentos(dto.getArgumentos()).build();
    }

    private RespostaFuncao toDomainResposta(RespostaFuncaoDTO dto) {
        if (dto == null) {
            return null;
        }
        return RespostaFuncao.builder().nome(dto.getNome()).resposta(dto.getResposta()).build();
    }

    public AssistenteMensagemResponse toResponse(RespostaAssistente resposta) {
        List<ChamadaFuncaoResponseDTO> chamadas = resposta.getChamadasFuncao() == null ? List.of()
                : resposta.getChamadasFuncao().stream()
                        .map(chamada -> ChamadaFuncaoResponseDTO.builder().nome(chamada.getNome())
                                .argumentos(chamada.getArgumentos()).build())
                        .toList();
        return AssistenteMensagemResponse.builder().texto(resposta.getTexto()).chamadasFuncao(chamadas).build();
    }
}
