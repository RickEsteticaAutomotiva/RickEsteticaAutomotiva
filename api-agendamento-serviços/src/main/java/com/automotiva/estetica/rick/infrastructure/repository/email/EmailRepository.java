package com.automotiva.estetica.rick.infrastructure.repository.email;

import com.automotiva.estetica.rick.infrastructure.entity.EmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<EmailEntity, Long> {
}
