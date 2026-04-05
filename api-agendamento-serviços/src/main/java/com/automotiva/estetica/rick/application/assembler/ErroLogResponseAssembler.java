package com.automotiva.estetica.rick.application.assembler;

import com.automotiva.estetica.rick.application.dto.response.ErroLogResponse;
import com.automotiva.estetica.rick.application.mapper.ErroLogDTOMapper;
import com.automotiva.estetica.rick.application.security.ErroLogRedactionService;
import com.automotiva.estetica.rick.domain.entity.ErroLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ErroLogResponseAssembler {

    private final ErroLogDTOMapper erroLogDTOMapper;
    private final ErroLogRedactionService erroLogRedactionService;

    public ErroLogResponse toRedactedResponse(ErroLog erroLog) {
        return erroLogRedactionService.redact(erroLogDTOMapper.toResponse(erroLog));
    }
}
