package com.example.diogoapi.dto;

import com.example.diogoapi.entity.StatusCandidatura;

import java.math.BigDecimal;

public record FuncionarioResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        String cargo,
        String departamento,
        BigDecimal salario,
        String cidade,
        StatusCandidatura status) {
}
