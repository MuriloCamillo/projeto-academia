package br.com.gymtime.dto;

/**
 * DTO (Data Transfer Object) para encapsular os dados de resposta de um Exercício.
 * Este record representa a visão de um exercício que é enviada para o cliente da API,
 * geralmente como parte de um {@link TreinoResponseDTO}.
 *
 * @param id               O identificador único do exercício.
 * @param nomeExercicio    O nome do exercício (ex: "Supino Reto").
 * @param seriesRepeticoes A descrição das séries e repetições (ex: "3x10").
 */
public record ExercicioResponseDTO(
        Long id,
        String nomeExercicio,
        String seriesRepeticoes
) {
}
