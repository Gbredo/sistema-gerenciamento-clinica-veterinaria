package com.clinica.infraestrutura.adaptador.notificacao;

import com.clinica.dominio.modelo.Animal;
import com.clinica.dominio.modelo.Consulta;
import com.clinica.dominio.porta.saida.PortaNotificacaoTutor;

public class NotificacaoConsole implements PortaNotificacaoTutor {

    @Override
    public void notificarAgendamento(String tutor, Animal animal, Consulta consulta) {
        System.out.printf(
                "[AGENDAMENTO] Tutor: %s | Animal: %s (%s) | Veterinário: %s | Data: %s às %s | Tipo: %s%n",
                tutor,
                animal.getNome(),
                animal.getRaca(),
                consulta.getVeterinario().getNome(),
                consulta.getData(),
                consulta.getHora(),
                consulta.getTipo()
        );
    }

    @Override
    public void notificarCancelamento(String tutor, Animal animal, String motivo) {
        System.out.printf(
                "[CANCELAMENTO] Tutor: %s | Animal: %s | Motivo: %s%n",
                tutor,
                animal.getNome(),
                motivo
        );
    }
}
