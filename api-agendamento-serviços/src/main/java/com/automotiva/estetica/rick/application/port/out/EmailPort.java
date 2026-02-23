package com.automotiva.estetica.rick.application.port.out;

import com.automotiva.estetica.rick.domain.entity.Email;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface EmailPort {

    void enviarEmailComAnexos(Email email, List<MultipartFile> anexos);
}
