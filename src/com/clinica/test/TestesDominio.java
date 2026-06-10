package com.clinica.test;

import com.clinica.dominio.excecao.AnimalNaoEncontradoException;
import com.clinica.dominio.excecao.VeterinarioIndisponivelException;
import com.clinica.dominio.modelo.Animal;
import com.clinica.dominio.modelo.Consulta;
import com.clinica.dominio.modelo.SituacaoConsulta;
import com.clinica.dominio.modelo.TipoConsulta;
import com.clinica.dominio.modelo.Veterinario;
import com.clinica.dominio.porta.saida.PortaAnimalRepositorio;
import com.clinica.dominio.porta.saida.PortaConsultaRepositorio;
import com.clinica.dominio.porta.saida.PortaNotificacaoTutor;
import com.clinica.dominio.porta.saida.PortaVeterinarioRepositorio;
import com.clinica.dominio.servico.ServicoAgendaConsulta;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TestesDominio {

    // ----------------------------------------------------------------
    // Fakes
    // ----------------------------------------------------------------

    static class FakeAnimalRepositorio implements PortaAnimalRepositorio {
        private final Map<Long, Animal> dados = new HashMap<>();
        private long proximoId = 1L;

        @Override
        public void salvar(Animal animal) {
            if (animal.getId() == null) animal.setId(proximoId++);
            dados.put(animal.getId(), animal);
        }

        @Override public Optional<Animal> buscarPorId(Long id) { return Optional.ofNullable(dados.get(id)); }
        @Override public List<Animal> listarPorTutor(String tutor) { return new ArrayList<>(dados.values()); }
        @Override public List<Animal> listarTodos() { return new ArrayList<>(dados.values()); }
        @Override public void remover(Long id) { dados.remove(id); }
    }

    static class FakeVeterinarioRepositorio implements PortaVeterinarioRepositorio {
        private final Map<Long, Veterinario> dados = new HashMap<>();

        FakeVeterinarioRepositorio() {
            Veterinario disponivel = new Veterinario(1L, "Dr. Disponível", "CRMV-SP 001", "Geral");
            dados.put(1L, disponivel);

            Veterinario ocupado = new Veterinario(2L, "Dr. Ocupado", "CRMV-SP 002", "Geral");
            ocupado.ocupar();
            dados.put(2L, ocupado);
        }

        @Override public void salvar(Veterinario vet) { dados.put(vet.getId(), vet); }
        @Override public Optional<Veterinario> buscarPorId(Long id) { return Optional.ofNullable(dados.get(id)); }
        @Override public List<Veterinario> buscarDisponiveis() {
            return dados.values().stream().filter(Veterinario::estaDisponivel).collect(Collectors.toList());
        }
        @Override public List<Veterinario> buscarPorEspecialidade(String esp) {
            return dados.values().stream()
                    .filter(v -> esp.equalsIgnoreCase(v.getEspecialidade()))
                    .collect(Collectors.toList());
        }
    }

    static class FakeConsultaRepositorio implements PortaConsultaRepositorio {
        private final Map<Long, Consulta> dados = new HashMap<>();
        private long proximoId = 1L;

        @Override
        public void salvar(Consulta consulta) {
            if (consulta.getId() == null) consulta.setId(proximoId++);
            dados.put(consulta.getId(), consulta);
        }

        @Override public Optional<Consulta> buscarPorId(Long id) { return Optional.ofNullable(dados.get(id)); }
        @Override public List<Consulta> buscarPorAnimal(Long animalId) {
            return dados.values().stream()
                    .filter(c -> c.getAnimal().getId().equals(animalId))
                    .collect(Collectors.toList());
        }
        @Override public List<Consulta> buscarPorVeterinario(Long vetId) {
            return dados.values().stream()
                    .filter(c -> c.getVeterinario().getId().equals(vetId))
                    .collect(Collectors.toList());
        }
        @Override public List<Consulta> listarAgendadas() {
            return dados.values().stream()
                    .filter(c -> c.getSituacao() == SituacaoConsulta.AGENDADA)
                    .collect(Collectors.toList());
        }
    }

    static class FakeNotificacao implements PortaNotificacaoTutor {
        int totalAgendamentos = 0;
        int totalCancelamentos = 0;

        @Override public void notificarAgendamento(String tutor, Animal animal, Consulta consulta) { totalAgendamentos++; }
        @Override public void notificarCancelamento(String tutor, Animal animal, String motivo) { totalCancelamentos++; }
    }

    // ----------------------------------------------------------------
    // Main
    // ----------------------------------------------------------------

    public static void main(String[] args) {
        FakeAnimalRepositorio animalRepo    = new FakeAnimalRepositorio();
        FakeVeterinarioRepositorio vetRepo  = new FakeVeterinarioRepositorio();
        FakeConsultaRepositorio consultaRepo = new FakeConsultaRepositorio();
        FakeNotificacao notif               = new FakeNotificacao();

        ServicoAgendaConsulta servico = new ServicoAgendaConsulta(animalRepo, vetRepo, consultaRepo, notif);

        Animal animal = new Animal(null, "Rex", "Cão", "Labrador",
                LocalDate.of(2019, 1, 1), "Carlos");
        animalRepo.salvar(animal);

        LocalDate data = LocalDate.of(2026, 7, 1);
        LocalTime hora = LocalTime.of(9, 0);

        Consulta consultaCenario1 = null;

        // ----------------------------------------------------------------
        System.out.println("\n--- Cenário 1: Agendamento bem-sucedido ---");
        // ----------------------------------------------------------------
        try {
            consultaCenario1 = servico.agendarConsulta(animal.getId(), 1L, data, hora, TipoConsulta.ROTINA);

            assert consultaCenario1.getSituacao() == SituacaoConsulta.AGENDADA
                    : "Consulta deveria estar AGENDADA";
            assert !vetRepo.buscarPorId(1L).get().estaDisponivel()
                    : "Vet 1 deveria estar OCUPADO após agendamento";

            System.out.println("[PASSOU]");
        } catch (Exception e) {
            System.out.println("[FALHOU] " + e.getMessage());
        }

        // ----------------------------------------------------------------
        System.out.println("\n--- Cenário 2: Veterinário indisponível ---");
        // ----------------------------------------------------------------
        try {
            servico.agendarConsulta(animal.getId(), 2L, data, hora.plusHours(1), TipoConsulta.ROTINA);
            assert false : "Deveria ter lançado VeterinarioIndisponivelException";
        } catch (VeterinarioIndisponivelException e) {
            System.out.println("[PASSOU] Exceção esperada: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[FALHOU] Exceção inesperada: " + e.getMessage());
        }

        // ----------------------------------------------------------------
        System.out.println("\n--- Cenário 3: Animal não encontrado ---");
        // ----------------------------------------------------------------
        try {
            servico.agendarConsulta(99L, 1L, data, hora.plusHours(2), TipoConsulta.EMERGENCIA);
            assert false : "Deveria ter lançado AnimalNaoEncontradoException";
        } catch (AnimalNaoEncontradoException e) {
            System.out.println("[PASSOU] Exceção esperada: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[FALHOU] Exceção inesperada: " + e.getMessage());
        }

        // ----------------------------------------------------------------
        System.out.println("\n--- Cenário 4: Transição inválida (cancelar e tentar realizar) ---");
        // ----------------------------------------------------------------
        try {
            servico.cancelarConsulta(consultaCenario1.getId());

            servico.realizarConsulta(consultaCenario1.getId(), "Observação pós-cancelamento");
            assert false : "Deveria ter lançado IllegalStateException";
        } catch (IllegalStateException e) {
            System.out.println("[PASSOU] Exceção esperada: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[FALHOU] Exceção inesperada: " + e.getMessage());
        }

        // ----------------------------------------------------------------
        System.out.println("\n--- Cenário 5: Cancelamento libera veterinário ---");
        // ----------------------------------------------------------------
        try {
            Veterinario vet1 = vetRepo.buscarPorId(1L).get();
            assert vet1.estaDisponivel() : "Vet 1 deveria estar DISPONÍVEL após cancelamento";
            System.out.println("[PASSOU]");
        } catch (AssertionError e) {
            System.out.println("[FALHOU] " + e.getMessage());
        }

        System.out.println("\n--- Resumo de notificações ---");
        System.out.println("Agendamentos notificados : " + notif.totalAgendamentos);
        System.out.println("Cancelamentos notificados: " + notif.totalCancelamentos);
    }
}
