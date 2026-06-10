package com.clinica.dominio.excecao;

public class VeterinarioIndisponivelException extends RuntimeException {

    public VeterinarioIndisponivelException(String nome) {
        super("Veterinário indisponível: " + nome);
    }
}
