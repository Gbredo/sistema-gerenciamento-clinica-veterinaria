package com.clinica.dominio.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Consulta {

    private final Long id;
    private final Animal animal;
    private final Veterinario veterinario;
    private final LocalDate data;
    private final LocalTime hora;
    private final TipoConsulta tipo;
    private SituacaoConsulta situacao;
    private String observacoes;

    public Consulta(Long id, Animal animal, Veterinario veterinario,
                    LocalDate data, LocalTime hora, TipoConsulta tipo) {
        if (animal == null) {
            throw new IllegalArgumentException("Animal não pode ser nulo.");
        }
        if (veterinario == null) {
            throw new IllegalArgumentException("Veterinário não pode ser nulo.");
        }
        if (data == null) {
            throw new IllegalArgumentException("Data da consulta não pode ser nula.");
        }
        if (hora == null) {
            throw new IllegalArgumentException("Hora da consulta não pode ser nula.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo da consulta não pode ser nulo.");
        }
        this.id = id;
        this.animal = animal;
        this.veterinario = veterinario;
        this.data = data;
        this.hora = hora;
        this.tipo = tipo;
        this.situacao = SituacaoConsulta.AGENDADA;
    }

    public void realizar(String observacoes) {
        if (situacao != SituacaoConsulta.AGENDADA) {
            throw new IllegalStateException(
                "Apenas consultas agendadas podem ser realizadas. Situação atual: " + situacao);
        }
        this.situacao = SituacaoConsulta.REALIZADA;
        this.observacoes = observacoes;
    }

    public void cancelar() {
        if (situacao == SituacaoConsulta.REALIZADA) {
            throw new IllegalStateException("Não é possível cancelar uma consulta já realizada.");
        }
        if (situacao == SituacaoConsulta.CANCELADA) {
            throw new IllegalStateException("Consulta já está cancelada.");
        }
        this.situacao = SituacaoConsulta.CANCELADA;
    }

    public Long getId() { return id; }
    public Animal getAnimal() { return animal; }
    public Veterinario getVeterinario() { return veterinario; }
    public LocalDate getData() { return data; }
    public LocalTime getHora() { return hora; }
    public TipoConsulta getTipo() { return tipo; }
    public SituacaoConsulta getSituacao() { return situacao; }
    public String getObservacoes() { return observacoes; }

    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
