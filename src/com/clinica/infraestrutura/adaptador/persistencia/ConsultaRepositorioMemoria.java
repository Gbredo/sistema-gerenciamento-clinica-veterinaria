package com.clinica.infraestrutura.adaptador.persistencia;

import com.clinica.dominio.modelo.Consulta;
import com.clinica.dominio.modelo.SituacaoConsulta;
import com.clinica.dominio.porta.saida.PortaConsultaRepositorio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConsultaRepositorioMemoria implements PortaConsultaRepositorio {

    private final Map<Long, Consulta> armazenamento = new HashMap<>();
    private long proximoId = 1L;

    @Override
    public void salvar(Consulta consulta) {
        Consulta paraArmazenar;
        if (consulta.getId() == null) {
            paraArmazenar = new Consulta(
                    proximoId++,
                    consulta.getAnimal(),
                    consulta.getVeterinario(),
                    consulta.getData(),
                    consulta.getHora(),
                    consulta.getTipo()
            );
        } else {
            paraArmazenar = consulta;
        }
        armazenamento.put(paraArmazenar.getId(), paraArmazenar);
    }

    @Override
    public Optional<Consulta> buscarPorId(Long id) {
        return Optional.ofNullable(armazenamento.get(id));
    }

    @Override
    public List<Consulta> buscarPorAnimal(Long animalId) {
        return armazenamento.values().stream()
                .filter(c -> c.getAnimal().getId().equals(animalId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Consulta> buscarPorVeterinario(Long vetId) {
        return armazenamento.values().stream()
                .filter(c -> c.getVeterinario().getId().equals(vetId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Consulta> listarAgendadas() {
        return armazenamento.values().stream()
                .filter(c -> c.getSituacao() == SituacaoConsulta.AGENDADA)
                .collect(Collectors.toList());
    }
}
