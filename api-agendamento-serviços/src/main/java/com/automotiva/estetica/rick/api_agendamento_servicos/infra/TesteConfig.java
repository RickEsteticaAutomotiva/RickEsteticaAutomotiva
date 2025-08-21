package com.automotiva.estetica.rick.api_agendamento_servicos.infra;

import com.automotiva.estetica.rick.api_agendamento_servicos.entity.PessoaEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
@Profile("dev")
public class TesteConfig implements CommandLineRunner {
    @Autowired
    private PessoaRepository pessoaRepository;

    @Override
    public void run(String... args) throws Exception {
        var pessoa1 = PessoaEntity.builder()
                .nome("Maria Silva")
                .email("maria@email.com")
                .dataNascimento(LocalDate.of(1990, 5, 10))
                .quantidade(5)
                .taxa(new BigDecimal("123.45"))
                .build();

        var pessoa2 = PessoaEntity.builder()
                .nome("João Souza")
                .email("joao@email.com")
                .dataNascimento(LocalDate.of(1985, 8, 20))
                .quantidade(10)
                .taxa(new BigDecimal("87.50"))
                .build();

        var pessoa3 = PessoaEntity.builder()
                .nome("Ana Lima")
                .email("ana@email.com")
                .dataNascimento(LocalDate.of(2000, 1, 15))
                .quantidade(2)
                .taxa(new BigDecimal("45.90"))
                .build();



        List<PessoaEntity> pessoas = List.of(pessoa1, pessoa2, pessoa3);

        pessoaRepository.saveAll(pessoas);
    }
}
