package br.com.gymtime.dto;

// Não precisa de anotações de validação para DTO de resposta
public record ExercicioResponseDTO(
        Long id,
        String nomeExercicio,
        String seriesRepeticoes
) {}