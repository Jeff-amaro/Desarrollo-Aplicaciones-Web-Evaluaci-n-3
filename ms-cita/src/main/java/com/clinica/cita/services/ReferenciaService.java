package com.clinica.cita.services;

import com.clinica.cita.exception.ReglaNegocioException;
import com.clinica.cita.exception.ServicioNoDisponibleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

/**
 * Verifica por HTTP que el paciente y el médico existan en sus propios microservicios.
 * ms-cita solo guarda los IDs; nunca consulta las bases de datos de los otros servicios.
 */
@Service
public class ReferenciaService {

    private final RestClient restClient = RestClient.create();

    @Value("${servicios.paciente-url}")
    private String pacienteUrl;

    @Value("${servicios.medico-url}")
    private String medicoUrl;

    @Value("${servicios.validar-referencias:true}")
    private boolean validarReferencias;

    public void validarPaciente(Long pacienteId) {
        validar(pacienteUrl + "/api/pacientes/" + pacienteId,
                "El paciente con id " + pacienteId + " no existe", "ms-paciente");
    }

    public void validarMedico(Long medicoId) {
        validar(medicoUrl + "/api/medicos/" + medicoId,
                "El médico con id " + medicoId + " no existe", "ms-medico");
    }

    private void validar(String url, String mensajeNoExiste, String nombreServicio) {
        if (!validarReferencias) {
            return;
        }
        try {
            restClient.get().uri(url).retrieve().toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ReglaNegocioException(mensajeNoExiste);
        } catch (ResourceAccessException e) {
            throw new ServicioNoDisponibleException("No se pudo conectar con " + nombreServicio
                    + ". Verifique que esté levantado.");
        }
    }
}