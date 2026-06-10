package com.clinica.dominio.porta.entrada;

import com.clinica.dominio.modelo.Consulta;
import com.clinica.dominio.modelo.TipoConsulta;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface PortaAgendaConsulta {

    Consulta agendarConsulta(Long animalId, Long veterinarioId, LocalDate data, LocalTime hora, TipoConsulta tipo);

    Consulta realizarConsulta(Long consultaId, String observacoes);

    void cancelarConsulta(Long consultaId);

    List<Consulta> obterHistoricoAnimal(Long animalId);

    List<Consulta> obterAgendaVeterinario(Long vetId);
}
