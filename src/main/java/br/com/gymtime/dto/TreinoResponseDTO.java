package br.com.gymtime.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO (Data Transfer Object) para encapsular os dados de resposta de um Treino.
 * Este record representa a visão completa de um treino que é enviada para o cliente da API,
 * incluindo seus exercícios e a referência ao aluno associado.
 *
 * @param id              O identificador único do treino.
 * @param nome            O nome do treino.
 * @param descricao       A descrição detalhada do treino.
 * @param dataCriacao     A data em que o treino foi criado.
 * @param dataAtualizacao A data da última atualização do treino.
 * @param alunoId         O ID do aluno ao qual este treino pertence.
 * @param exercicios      A lista de exercícios que compõem o treino.
 */
public record TreinoResponseDTO(
        Long id,
        String nome,
        String descricao,
        LocalDate dataCriacao,
        LocalDate dataAtualizacao,
        Long alunoId,
        List<ExercicioResponseDTO> exercicios
) {
}
