package com.example.diogoapi.dto;

public record IndicadoresResponse(
        long total,
        long emAnalise,
        long aprovados,
        long reprovados,
        long contratados) {
}
