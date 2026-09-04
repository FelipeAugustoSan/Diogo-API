package com.example.diogoapi.dto;

import com.example.diogoapi.entity.StatusCandidatura;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record FuncionarioPatchRequest(

        @Size(max = 80, message = "O cargo deve ter no máximo 80 caracteres")
        String cargo,

        @PositiveOrZero(message = "O salário não pode ser negativo")
        BigDecimal salario,

        StatusCandidatura status) {
}
