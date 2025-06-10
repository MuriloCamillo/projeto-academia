package br.com.gymtime.dto;

import java.time.LocalDate;
import java.util.List;

public record TreinoResponseDTO(
        Long id,
        String nome,
        String descricao,
        LocalDate dataCriacao,
        LocalDate dataAtualizacao,
        Long alunoId,
        List<ExercicioResponseDTO> exercicios // Lista de DTOs de exercício
) {
}