package com.automotiva.estetica.rick.adapter.out.persistence.email;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.EmailJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface EmailJpaRepository extends JpaRepository<EmailJpaEntity, Long> {}
