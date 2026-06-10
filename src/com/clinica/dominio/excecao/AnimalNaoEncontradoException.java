package com.clinica.dominio.excecao;

public class AnimalNaoEncontradoException extends RuntimeException {

    public AnimalNaoEncontradoException(String id) {
        super("Animal não encontrado com o identificador: " + id);
    }
}
