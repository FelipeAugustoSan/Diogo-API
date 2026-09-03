package com.example.diogoapi.entity;

public enum StatusCandidatura {

    EM_ANALISE,
    APROVADO,
    REPROVADO,
    CONTRATADO;

    public static StatusCandidatura padrao() {
        return EM_ANALISE;
    }
}
