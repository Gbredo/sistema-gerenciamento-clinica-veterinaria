package com.clinica.dominio.servico;

import com.clinica.dominio.excecao.AnimalNaoEncontradoException;
import com.clinica.dominio.excecao.VeterinarioIndisponivelException;
import com.clinica.dominio.modelo.Animal;
import com.clinica.dominio.modelo.Consulta;
import com.clinica.dominio.modelo.TipoConsulta;
import com.clinica.dominio.modelo.Veterinario;
import com.clinica.dominio.porta.entrada.PortaAgendaConsulta;
import com.clinica.dominio.porta.saida.PortaAnimalRepositorio;
import com.clinica.dominio.porta.saida.PortaConsultaRepositorio;
import com.clinica.dominio.porta.saida.PortaNotificacaoTutor;
import com.clinica.dominio.porta.saida.PortaVeterinarioRepositorio;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ServicoAgendaConsulta implements PortaAgendaConsulta {

    private final PortaAnimalRepositorio animalRepositorio;
    private final PortaVeterinarioRepositorio veterinarioRepositorio;
    private final PortaConsultaRepositorio consultaRepositorio;
    private final PortaNotificacaoTutor notificacaoTutor;

    public ServicoAgendaConsulta(PortaAnimalRepositorio animalRepositorio,
                                 PortaVeterinarioRepositorio veterinarioRepositorio,
                                 PortaConsultaRepositorio consultaRepositorio,
                                 PortaNotificacaoTutor notificacaoTutor) {
        this.animalRepositorio = animalRepositorio;
        this.veterinarioRepositorio = veterinarioRepositorio;
        this.consultaRepositorio = consultaRepositorio;
        this.notificacaoTutor = notificacaoTutor;
    }

    @Override
    public Consulta agendarConsulta(Long animalId, Long veterinarioId,
                                    LocalDate data, LocalTime hora, TipoConsulta tipo) {
        Animal animal = animalRepositorio.buscarPorId(animalId)
                .orElseThrow(() -> new AnimalNaoEncontradoException(String.valueOf(animalId)));

        Veterinario veterinario = veterinarioRepositorio.buscarPorId(veterinarioId)
                .orElseThrow(() -> new VeterinarioIndisponivelException("ID " + veterinarioId + " não encontrado"));

        if (!veterinario.estaDisponivel()) {
            throw new VeterinarioIndisponivelException(veterinario.getNome());
        }

        veterinario.ocupar();

        Consulta consulta = new Consulta(null, animal, veterinario, data, hora, tipo);
        consultaRepositorio.salvar(consulta);

        notificacaoTutor.notificarAgendamento(animal.getTutor(), animal, consulta);

        return consulta;
    }

    @Override
    public Consulta realizarConsulta(Long consultaId, String observacoes) {
        Consulta consulta = consultaRepositorio.buscarPorId(consultaId)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada com o identificador: " + consultaId));

        consulta.realizar(observacoes);
        consulta.getVeterinario().liberar();
        consultaRepositorio.salvar(consulta);

        return consulta;
    }

    @Override
    public void cancelarConsulta(Long consultaId) {
        Consulta consulta = consultaRepositorio.buscarPorId(consultaId)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada com o identificador: " + consultaId));

        Animal animal = consulta.getAnimal();

        consulta.cancelar();
        consulta.getVeterinario().liberar();

        notificacaoTutor.notificarCancelamento(animal.getTutor(), animal, "Cancelamento solicitado");
        consultaRepositorio.salvar(consulta);
    }

    @Override
    public List<Consulta> obterHistoricoAnimal(Long animalId) {
        return consultaRepositorio.buscarPorAnimal(animalId);
    }

    @Override
    public List<Consulta> obterAgendaVeterinario(Long vetId) {
        return consultaRepositorio.buscarPorVeterinario(vetId);
    }
}
