package com.clinica.paciente.services;

import com.clinica.paciente.dto.PacienteDTO;
import com.clinica.paciente.exception.RecursoNoEncontradoException;
import com.clinica.paciente.exception.ReglaNegocioException;
import com.clinica.paciente.model.Paciente;
import com.clinica.paciente.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository repository;

    public PacienteService(PacienteRepository repository) {
        this.repository = repository;
    }

    public Paciente registrar(PacienteDTO dto) {
        if (repository.existsByDni(dto.getDni())) {
            throw new ReglaNegocioException("Ya existe un paciente con el DNI " + dto.getDni());
        }
        Paciente paciente = new Paciente();
        copiarDatos(dto, paciente);
        return repository.save(paciente);
    }

    public List<Paciente> listar() {
        return repository.findAll();
    }

    public Paciente buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente con id " + id + " no encontrado"));
    }

    public Paciente actualizar(Long id, PacienteDTO dto) {
        Paciente paciente = buscarPorId(id);
        if (repository.existsByDniAndIdNot(dto.getDni(), id)) {
            throw new ReglaNegocioException("Ya existe otro paciente con el DNI " + dto.getDni());
        }
        copiarDatos(dto, paciente);
        return repository.save(paciente);
    }

    public void eliminar(Long id) {
        Paciente paciente = buscarPorId(id);
        repository.delete(paciente);
    }

    private void copiarDatos(PacienteDTO dto, Paciente paciente) {
        paciente.setNombres(dto.getNombres());
        paciente.setApellidos(dto.getApellidos());
        paciente.setDni(dto.getDni());
        paciente.setTelefono(dto.getTelefono());
        paciente.setEmail(dto.getEmail());
    }
}