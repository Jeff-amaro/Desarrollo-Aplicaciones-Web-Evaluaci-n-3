package com.clinica.cita.repository;

import com.clinica.cita.model.Cita;
import com.clinica.cita.model.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByPacienteId(Long pacienteId);

    List<Cita> findByMedicoId(Long medicoId);

    boolean existsByMedicoIdAndFechaAndHoraAndEstadoNot(Long medicoId, LocalDate fecha, LocalTime hora, EstadoCita estado);

    boolean existsByMedicoIdAndFechaAndHoraAndEstadoNotAndIdNot(Long medicoId, LocalDate fecha, LocalTime hora,
                                                                EstadoCita estado, Long id);
}