package com.clinica.medico.services;

import com.clinica.medico.dto.MedicoDTO;
import com.clinica.medico.exception.RecursoNoEncontradoException;
import com.clinica.medico.exception.ReglaNegocioException;
import com.clinica.medico.model.Medico;
import com.clinica.medico.repository.MedicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicoService {

    private final MedicoRepository repository;

    public MedicoService(MedicoRepository repository) {
        this.repository = repository;
    }

    public Medico registrar(MedicoDTO dto) {
        if (repository.existsByCmp(dto.getCmp())) {
            throw new ReglaNegocioException("Ya existe un médico con el CMP " + dto.getCmp());
        }
        Medico medico = new Medico();
        copiarDatos(dto, medico);
        return repository.save(medico);
    }

    public List<Medico> listar() {
        return repository.findAll();
    }

    public Medico buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Médico con id " + id + " no encontrado"));
    }

    public Medico actualizar(Long id, MedicoDTO dto) {
        Medico medico = buscarPorId(id);
        if (repository.existsByCmpAndIdNot(dto.getCmp(), id)) {
            throw new ReglaNegocioException("Ya existe otro médico con el CMP " + dto.getCmp());
        }
        copiarDatos(dto, medico);
        return repository.save(medico);
    }

    public void eliminar(Long id) {
        Medico medico = buscarPorId(id);
        repository.delete(medico);
    }

    private void copiarDatos(MedicoDTO dto, Medico medico) {
        medico.setNombres(dto.getNombres());
        medico.setApellidos(dto.getApellidos());
        medico.setCmp(dto.getCmp());
        medico.setEspecialidad(dto.getEspecialidad());
        medico.setTelefono(dto.getTelefono());
        medico.setEmail(dto.getEmail());
    }
}