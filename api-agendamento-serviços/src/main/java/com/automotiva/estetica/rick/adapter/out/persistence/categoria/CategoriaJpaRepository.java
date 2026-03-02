package com.automotiva.estetica.rick.adapter.out.persistence.categoria;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.CategoriaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface CategoriaJpaRepository extends JpaRepository<CategoriaJpaEntity, Long> {}
