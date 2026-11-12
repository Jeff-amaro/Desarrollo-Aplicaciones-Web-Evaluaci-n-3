package com.clinica.medico.repository;

import com.clinica.medico.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    boolean existsByCmp(String cmp);

    boolean existsByCmpAndIdNot(String cmp, Long id);
}