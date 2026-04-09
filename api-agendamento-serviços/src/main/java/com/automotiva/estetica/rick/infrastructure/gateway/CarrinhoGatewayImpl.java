package com.automotiva.estetica.rick.infrastructure.gateway;

import com.automotiva.estetica.rick.infrastructure.repository.carrinho.CarrinhoRepository;
import com.automotiva.estetica.rick.infrastructure.entity.CarrinhoEntity;
import com.automotiva.estetica.rick.infrastructure.mapper.CarrinhoEntityMapper;
import com.automotiva.estetica.rick.domain.entity.Carrinho;
import com.automotiva.estetica.rick.domain.gateway.CarrinhoGateway;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarrinhoGatewayImpl implements CarrinhoGateway {

    private final CarrinhoRepository carrinhoRepository;
    private final CarrinhoEntityMapper carrinhoEntityMapper;

    @Override
    public Carrinho salvar(Carrinho carrinho) {
        CarrinhoEntity entity = carrinhoEntityMapper.toEntity(carrinho);
        return carrinhoEntityMapper.toDomain(carrinhoRepository.save(entity));
    }

    @Override
    public Optional<Carrinho> buscarPorId(Long id) {
        return carrinhoRepository.findById(id).map(carrinhoEntityMapper::toDomain);
    }

    @Override
    public List<Carrinho> buscarPorPessoaId(Long pessoaId) {
        return carrinhoRepository.findByPessoaId(pessoaId).stream().map(carrinhoEntityMapper::toDomain).toList();
    }

    @Override
    public boolean existePorPessoaEServico(Long pessoaId, Long servicoId) {
        return carrinhoRepository.existsByPessoaIdAndServicoId(pessoaId, servicoId);
    }

    @Override
    public void deletarPorId(Long id) {
        carrinhoRepository.deleteById(id);
    }

    @Override
    public void deletarTodos(List<Carrinho> itens) {
        carrinhoRepository.deleteAll(itens.stream().map(carrinhoEntityMapper::toEntity).toList());
    }
}
