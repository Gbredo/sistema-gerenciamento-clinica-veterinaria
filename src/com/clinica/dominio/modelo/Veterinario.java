package com.clinica.dominio.modelo;

import com.clinica.dominio.excecao.VeterinarioIndisponivelException;

public class Veterinario {

    private enum SituacaoVeterinario { DISPONIVEL, OCUPADO }

    private final Long id;
    private final String nome;
    private final String crmv;
    private final String especialidade;
    private SituacaoVeterinario situacao;

    public Veterinario(Long id, String nome, String crmv, String especialidade) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do veterinário não pode ser vazio.");
        }
        if (crmv == null || crmv.isBlank()) {
            throw new IllegalArgumentException("CRMV não pode ser vazio.");
        }
        this.id = id;
        this.nome = nome;
        this.crmv = crmv;
        this.especialidade = especialidade;
        this.situacao = SituacaoVeterinario.DISPONIVEL;
    }

    public boolean estaDisponivel() {
        return situacao == SituacaoVeterinario.DISPONIVEL;
    }

    public void ocupar() {
        if (!estaDisponivel()) {
            throw new VeterinarioIndisponivelException(nome);
        }
        this.situacao = SituacaoVeterinario.OCUPADO;
    }

    public void liberar() {
        this.situacao = SituacaoVeterinario.DISPONIVEL;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCrmv() { return crmv; }
    public String getEspecialidade() { return especialidade; }
    public String getSituacao() { return situacao.name(); }
}
