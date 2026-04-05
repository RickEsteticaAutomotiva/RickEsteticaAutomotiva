package com.automotiva.estetica.rick.infrastructure.repository.categoria;

import com.automotiva.estetica.rick.infrastructure.entity.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<CategoriaEntity, Long> {
}
