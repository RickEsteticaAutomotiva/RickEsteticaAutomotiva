package config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

@Component
public class AutenticacaoEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        String json = String.format(
                "{\"error\": \"Não autorizado\", \"message\": \"%s\"}",
                authException.getMessage() != null ? authException.getMessage() : "Acesso negado"
        );

        try {
            response.getWriter().write(json);
            response.getWriter().flush();
        } catch (IOException e) {
            // Log do erro se necessário
            e.printStackTrace();
        }
    }
}
