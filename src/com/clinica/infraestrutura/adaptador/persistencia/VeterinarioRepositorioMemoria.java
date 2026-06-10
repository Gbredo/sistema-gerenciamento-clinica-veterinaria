package com.clinica.infraestrutura.adaptador.persistencia;

import com.clinica.dominio.modelo.Veterinario;
import com.clinica.dominio.porta.saida.PortaVeterinarioRepositorio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class VeterinarioRepositorioMemoria implements PortaVeterinarioRepositorio {

    private final Map<Long, Veterinario> armazenamento = new HashMap<>();
    private long proximoId = 1L;

    @Override
    public void salvar(Veterinario vet) {
        Veterinario paraArmazenar;
        if (vet.getId() == null) {
            paraArmazenar = new Veterinario(
                    proximoId++,
                    vet.getNome(),
                    vet.getCrmv(),
                    vet.getEspecialidade()
            );
        } else {
            paraArmazenar = vet;
        }
        armazenamento.put(paraArmazenar.getId(), paraArmazenar);
    }

    @Override
    public Optional<Veterinario> buscarPorId(Long id) {
        return Optional.ofNullable(armazenamento.get(id));
    }

    @Override
    public List<Veterinario> buscarDisponiveis() {
        return armazenamento.values().stream()
                .filter(Veterinario::estaDisponivel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Veterinario> buscarPorEspecialidade(String especialidade) {
        return armazenamento.values().stream()
                .filter(v -> v.getEspecialidade() != null
                        && v.getEspecialidade().equalsIgnoreCase(especialidade))
                .collect(Collectors.toList());
    }
}
