package com.clinica.cita.services;

import com.clinica.cita.dto.CitaDTO;
import com.clinica.cita.exception.RecursoNoEncontradoException;
import com.clinica.cita.exception.ReglaNegocioException;
import com.clinica.cita.model.Cita;
import com.clinica.cita.model.EstadoCita;
import com.clinica.cita.repository.CitaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CitaService {

    private final CitaRepository repository;
    private final ReferenciaService referenciaService;

    public CitaService(CitaRepository repository, ReferenciaService referenciaService) {
        this.repository = repository;
        this.referenciaService = referenciaService;
    }

    public Cita registrar(CitaDTO dto) {
        referenciaService.validarPaciente(dto.getPacienteId());
        referenciaService.validarMedico(dto.getMedicoId());

        if (repository.existsByMedicoIdAndFechaAndHoraAndEstadoNot(
                dto.getMedicoId(), dto.getFecha(), dto.getHora(), EstadoCita.CANCELADA)) {
            throw new ReglaNegocioException("El médico ya tiene una cita en esa fecha y hora");
        }

        Cita cita = new Cita();
        copiarDatos(dto, cita);
        if (cita.getEstado() == null) {
            cita.setEstado(EstadoCita.PROGRAMADA);
        }
        return repository.save(cita);
    }

    public List<Cita> listar() {
        return repository.findAll();
    }

    public Cita buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita con id " + id + " no encontrada"));
    }

    public List<Cita> listarPorPaciente(Long pacienteId) {
        return repository.findByPacienteId(pacienteId);
    }

    public List<Cita> listarPorMedico(Long medicoId) {
        return repository.findByMedicoId(medicoId);
    }

    public Cita actualizar(Long id, CitaDTO dto) {
        Cita cita = buscarPorId(id);
        referenciaService.validarPaciente(dto.getPacienteId());
        referenciaService.validarMedico(dto.getMedicoId());

        if (repository.existsByMedicoIdAndFechaAndHoraAndEstadoNotAndIdNot(
                dto.getMedicoId(), dto.getFecha(), dto.getHora(), EstadoCita.CANCELADA, id)) {
            throw new ReglaNegocioException("El médico ya tiene una cita en esa fecha y hora");
        }

        copiarDatos(dto, cita);
        if (cita.getEstado() == null) {
            cita.setEstado(EstadoCita.PROGRAMADA);
        }
        return repository.save(cita);
    }

    public Cita cambiarEstado(Long id, EstadoCita nuevoEstado) {
        Cita cita = buscarPorId(id);
        if (cita.getEstado() == EstadoCita.CANCELADA || cita.getEstado() == EstadoCita.ATENDIDA) {
            throw new ReglaNegocioException("No se puede cambiar el estado de una cita "
                    + cita.getEstado());
        }
        cita.setEstado(nuevoEstado);
        return repository.save(cita);
    }

    public void eliminar(Long id) {
        Cita cita = buscarPorId(id);
        repository.delete(cita);
    }

    private void copiarDatos(CitaDTO dto, Cita cita) {
        cita.setFecha(dto.getFecha());
        cita.setHora(dto.getHora());
        cita.setPacienteId(dto.getPacienteId());
        cita.setMedicoId(dto.getMedicoId());
        cita.setMotivo(dto.getMotivo());
        if (dto.getEstado() != null) {
            cita.setEstado(dto.getEstado());
        }
    }
}