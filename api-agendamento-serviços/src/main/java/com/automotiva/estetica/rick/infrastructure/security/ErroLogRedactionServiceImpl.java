package com.automotiva.estetica.rick.infrastructure.security;

import com.automotiva.estetica.rick.application.dto.response.ErroLogResponse;
import com.automotiva.estetica.rick.application.security.ErroLogRedactionService;
import org.springframework.stereotype.Component;

@Component
public class ErroLogRedactionServiceImpl implements ErroLogRedactionService {

    @Override
    public ErroLogResponse redact(ErroLogResponse response) {
        return ErroLogResponseRedactor.redactResponse(response);
    }
}
