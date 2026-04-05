package com.automotiva.estetica.rick.application.security;

import com.automotiva.estetica.rick.application.dto.response.ErroLogResponse;

public interface ErroLogRedactionService {

    ErroLogResponse redact(ErroLogResponse response);
}
