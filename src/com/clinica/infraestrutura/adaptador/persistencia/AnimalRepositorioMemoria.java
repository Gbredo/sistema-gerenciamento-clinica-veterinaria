package com.clinica.infraestrutura.adaptador.persistencia;

import com.clinica.dominio.modelo.Animal;
import com.clinica.dominio.porta.saida.PortaAnimalRepositorio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnimalRepositorioMemoria implements PortaAnimalRepositorio {

    private final Map<Long, Animal> armazenamento = new HashMap<>();
    private long proximoId = 1L;

    @Override
    public void salvar(Animal animal) {
        if (animal.getId() == null) {
            animal.setId(proximoId++);
        }
        armazenamento.put(animal.getId(), animal);
    }

    @Override
    public Optional<Animal> buscarPorId(Long id) {
        return Optional.ofNullable(armazenamento.get(id));
    }

    @Override
    public List<Animal> listarPorTutor(String tutor) {
        return armazenamento.values().stream()
                .filter(a -> a.getTutor().equalsIgnoreCase(tutor))
                .collect(Collectors.toList());
    }

    @Override
    public List<Animal> listarTodos() {
        return new ArrayList<>(armazenamento.values());
    }

    @Override
    public void remover(Long id) {
        armazenamento.remove(id);
    }
}
