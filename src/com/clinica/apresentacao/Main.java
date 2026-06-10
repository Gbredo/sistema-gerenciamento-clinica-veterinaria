package com.clinica.apresentacao;

import com.clinica.dominio.modelo.Animal;
import com.clinica.dominio.modelo.Consulta;
import com.clinica.dominio.modelo.TipoConsulta;
import com.clinica.dominio.modelo.Veterinario;
import com.clinica.dominio.servico.ServicoAgendaConsulta;
import com.clinica.infraestrutura.adaptador.notificacao.NotificacaoConsole;
import com.clinica.infraestrutura.adaptador.notificacao.NotificacaoCsv;
import com.clinica.infraestrutura.adaptador.persistencia.AnimalRepositorioMemoria;
import com.clinica.infraestrutura.adaptador.persistencia.ConsultaRepositorioMemoria;
import com.clinica.infraestrutura.adaptador.persistencia.VeterinarioRepositorioMemoria;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // --- Composition Root ---
        AnimalRepositorioMemoria animalRepo = new AnimalRepositorioMemoria();
        VeterinarioRepositorioMemoria vetRepo = new VeterinarioRepositorioMemoria();
        ConsultaRepositorioMemoria consultaRepo = new ConsultaRepositorioMemoria();

        NotificacaoConsole notifConsole = new NotificacaoConsole();

        ServicoAgendaConsulta servicoConsole = new ServicoAgendaConsulta(
                animalRepo, vetRepo, consultaRepo, notifConsole);

        // ----------------------------------------------------------------
        separador("PASSO 1 — Cadastro de Animais");
        // ----------------------------------------------------------------

        Animal rex = new Animal(null, "Rex", "Cão", "Labrador",
                LocalDate.of(2019, 3, 15), "Carlos Silva");
        Animal mimi = new Animal(null, "Mimi", "Gato", "Persa",
                LocalDate.of(2021, 7, 20), "Ana Souza");

        animalRepo.salvar(rex);
        animalRepo.salvar(mimi);

        rex  = animalRepo.buscarPorId(1L).get();
        mimi = animalRepo.buscarPorId(2L).get();

        System.out.println("Animal 1 cadastrado: " + rex.getNome()  + " (ID " + rex.getId()  + ")");
        System.out.println("Animal 2 cadastrado: " + mimi.getNome() + " (ID " + mimi.getId() + ")");

        // ----------------------------------------------------------------
        separador("PASSO 2 — Cadastro de Veterinários");
        // ----------------------------------------------------------------

        Veterinario drJoao  = new Veterinario(null, "Dr. João Martins", "CRMV-SP 12345", "Clínica Geral");
        Veterinario draMaria = new Veterinario(null, "Dra. Maria Lopes", "CRMV-SP 67890", "Cardiologia");

        vetRepo.salvar(drJoao);
        vetRepo.salvar(draMaria);

        drJoao   = vetRepo.buscarPorId(1L).get();
        draMaria = vetRepo.buscarPorId(2L).get();

        System.out.println("Veterinário 1 cadastrado: " + drJoao.getNome()   + " (ID " + drJoao.getId()   + ")");
        System.out.println("Veterinário 2 cadastrado: " + draMaria.getNome() + " (ID " + draMaria.getId() + ")");

        // ----------------------------------------------------------------
        separador("PASSO 3 — Agendamento de Consulta de ROTINA (notificação Console)");
        // ----------------------------------------------------------------

        Consulta consulta1 = servicoConsole.agendarConsulta(
                rex.getId(), drJoao.getId(),
                LocalDate.of(2026, 6, 15), LocalTime.of(9, 0),
                TipoConsulta.ROTINA);

        System.out.println("Consulta 1 agendada — ID: " + consulta1.getId()
                + " | Situação: " + consulta1.getSituacao());

        // ----------------------------------------------------------------
        separador("TROCA DE ADAPTADOR — Notificação via CSV");
        // ----------------------------------------------------------------

        NotificacaoCsv notifCsv = new NotificacaoCsv("notificacoes.csv");

        ServicoAgendaConsulta servicoCsv = new ServicoAgendaConsulta(
                animalRepo, vetRepo, consultaRepo, notifCsv);

        // ----------------------------------------------------------------
        separador("PASSO 4 — Agendamento de Consulta de EMERGENCIA (notificação CSV)");
        // ----------------------------------------------------------------

        Consulta consulta2 = servicoCsv.agendarConsulta(
                mimi.getId(), draMaria.getId(),
                LocalDate.of(2026, 6, 15), LocalTime.of(10, 30),
                TipoConsulta.EMERGENCIA);

        System.out.println("Consulta 2 agendada — ID: " + consulta2.getId()
                + " | Situação: " + consulta2.getSituacao());
        System.out.println("(Verifique o arquivo notificacoes.csv gerado na raiz do projeto)");

        // ----------------------------------------------------------------
        separador("PASSO 5 — Realizar Consulta 1");
        // ----------------------------------------------------------------

        Consulta consulta1Realizada = servicoConsole.realizarConsulta(
                consulta1.getId(), "Animal saudável. Vacinação em dia. Retorno em 6 meses.");

        System.out.println("Consulta 1 realizada — Situação: " + consulta1Realizada.getSituacao());
        System.out.println("Observações: " + consulta1Realizada.getObservacoes());

        // ----------------------------------------------------------------
        separador("PASSO 6 — Cancelar Consulta 2 e verificar disponibilidade");
        // ----------------------------------------------------------------

        servicoCsv.cancelarConsulta(consulta2.getId());

        Veterinario draMariaPosCancel = vetRepo.buscarPorId(draMaria.getId()).get();
        System.out.println("Consulta 2 cancelada.");
        System.out.println("Dra. Maria disponível após cancelamento? " + draMariaPosCancel.estaDisponivel());

        // ----------------------------------------------------------------
        separador("PASSO 7 — Histórico de consultas do Animal 1 (Rex)");
        // ----------------------------------------------------------------

        List<Consulta> historicoRex = servicoConsole.obterHistoricoAnimal(rex.getId());
        historicoRex.forEach(c -> System.out.printf(
                "  ID: %d | Tipo: %-10s | Situação: %-10s | Data: %s às %s%n",
                c.getId(), c.getTipo(), c.getSituacao(), c.getData(), c.getHora()));

        // ----------------------------------------------------------------
        separador("PASSO 8 — Agenda do Veterinário 1 (Dr. João)");
        // ----------------------------------------------------------------

        List<Consulta> agendaJoao = servicoConsole.obterAgendaVeterinario(drJoao.getId());
        agendaJoao.forEach(c -> System.out.printf(
                "  ID: %d | Animal: %-6s | Tipo: %-10s | Situação: %-10s | Data: %s às %s%n",
                c.getId(), c.getAnimal().getNome(), c.getTipo(), c.getSituacao(), c.getData(), c.getHora()));
    }

    private static void separador(String titulo) {
        System.out.println("\n========================================");
        System.out.println("  " + titulo);
        System.out.println("========================================");
    }
}
