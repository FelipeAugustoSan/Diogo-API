package com.example.diogoapi.dto;

import com.example.diogoapi.entity.StatusCandidatura;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record FuncionarioRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail informado é inválido")
        @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres")
        String email,

        @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres")
        String telefone,

        @NotBlank(message = "O cargo é obrigatório")
        @Size(max = 80, message = "O cargo deve ter no máximo 80 caracteres")
        String cargo,

        @Size(max = 80, message = "O departamento deve ter no máximo 80 caracteres")
        String departamento,

        @PositiveOrZero(message = "O salário não pode ser negativo")
        BigDecimal salario,

        @Size(max = 80, message = "A cidade deve ter no máximo 80 caracteres")
        String cidade,

        StatusCandidatura status) {
}
