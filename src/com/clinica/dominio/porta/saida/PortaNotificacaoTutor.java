package com.clinica.dominio.porta.saida;

import com.clinica.dominio.modelo.Animal;
import com.clinica.dominio.modelo.Consulta;

public interface PortaNotificacaoTutor {

    void notificarAgendamento(String tutor, Animal animal, Consulta consulta);

    void notificarCancelamento(String tutor, Animal animal, String motivo);
}
