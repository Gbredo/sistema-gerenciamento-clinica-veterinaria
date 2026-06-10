package com.clinica.infraestrutura.adaptador.notificacao;

import com.clinica.dominio.modelo.Animal;
import com.clinica.dominio.modelo.Consulta;
import com.clinica.dominio.porta.saida.PortaNotificacaoTutor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class NotificacaoCsv implements PortaNotificacaoTutor {

    private final String caminhoArquivo;

    public NotificacaoCsv(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    @Override
    public void notificarAgendamento(String tutor, Animal animal, Consulta consulta) {
        String linha = String.format("%s,\"AGENDAMENTO\",%s,%s,%s,%sT%s\n",
                LocalDateTime.now(),
                tutor,
                animal.getNome(),
                consulta.getVeterinario().getNome(),
                consulta.getData(),
                consulta.getHora()
        );
        escrever(linha);
    }

    @Override
    public void notificarCancelamento(String tutor, Animal animal, String motivo) {
        String linha = String.format("%s,\"CANCELAMENTO\",%s,%s,\"N/A\",%s\n",
                LocalDateTime.now(),
                tutor,
                animal.getNome(),
                motivo
        );
        escrever(linha);
    }

    private static final String CABECALHO = "timestamp,tipo_evento,tutor,animal,veterinario,data_consulta\n";

    private void escrever(String linha) {
        File arquivo = new File(caminhoArquivo);
        try (FileWriter fw = new FileWriter(arquivo, true)) {
            if (!arquivo.exists() || arquivo.length() == 0) {
                fw.write(CABECALHO);
            }
            fw.write(linha);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao escrever notificação no arquivo: " + caminhoArquivo, e);
        }
    }
}
