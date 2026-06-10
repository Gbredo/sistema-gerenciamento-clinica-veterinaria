package com.clinica.dominio.porta.saida;

import com.clinica.dominio.modelo.Consulta;

import java.util.List;
import java.util.Optional;

public interface PortaConsultaRepositorio {

    void salvar(Consulta consulta);

    Optional<Consulta> buscarPorId(Long id);

    List<Consulta> buscarPorAnimal(Long animalId);

    List<Consulta> buscarPorVeterinario(Long vetId);

    List<Consulta> listarAgendadas();
}
