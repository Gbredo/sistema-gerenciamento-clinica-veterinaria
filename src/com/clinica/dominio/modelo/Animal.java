package com.clinica.dominio.modelo;

import java.time.LocalDate;
import java.time.Period;

public class Animal {

    private final Long id;
    private String nome;
    private String especie;
    private String raca;
    private LocalDate dataNascimento;
    private String tutor;

    public Animal(Long id, String nome, String especie, String raca,
                  LocalDate dataNascimento, String tutor) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do animal não pode ser vazio.");
        }
        if (especie == null || especie.isBlank()) {
            throw new IllegalArgumentException("Espécie do animal não pode ser vazia.");
        }
        if (tutor == null || tutor.isBlank()) {
            throw new IllegalArgumentException("Tutor do animal não pode ser vazio.");
        }
        this.id = id;
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.dataNascimento = dataNascimento;
        this.tutor = tutor;
    }

    public int calcularIdadeEmAnos() {
        if (dataNascimento == null) {
            throw new IllegalStateException("Data de nascimento não informada para calcular a idade.");
        }
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEspecie() { return especie; }
    public String getRaca() { return raca; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public String getTutor() { return tutor; }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do animal não pode ser vazio.");
        }
        this.nome = nome;
    }

    public void setEspecie(String especie) {
        if (especie == null || especie.isBlank()) {
            throw new IllegalArgumentException("Espécie do animal não pode ser vazia.");
        }
        this.especie = especie;
    }

    public void setRaca(String raca) { this.raca = raca; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public void setTutor(String tutor) {
        if (tutor == null || tutor.isBlank()) {
            throw new IllegalArgumentException("Tutor do animal não pode ser vazio.");
        }
        this.tutor = tutor;
    }
}
