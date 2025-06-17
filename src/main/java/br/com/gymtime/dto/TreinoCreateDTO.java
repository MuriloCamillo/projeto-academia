package br.com.gymtime.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO (Data Transfer Object) para encapsular os dados de criação de um novo Treino.
 * Este record é utilizado para receber os dados via requisição REST, incluindo os
 * detalhes do treino, a lista de exercícios e o ID do aluno ao qual ele pertence.
 *
 * @param nome       O nome do treino.
 * @param descricao  Uma descrição opcional para o treino.
 * @param alunoId    O ID do aluno que será associado a este treino. É obrigatório.
 * @param exercicios Uma lista de exercícios que compõem o treino. A validação será aplicada a cada item da lista.
 */
public record TreinoCreateDTO(
        /**
         * Nome do treino.
         * - Não pode ser nulo ou em branco.
         * - Deve ter entre 3 e 100 caracteres.
         */
        @NotBlank(message = "O nome do treino não pode estar em branco.")
        @Size(min = 3, max = 100, message = "O nome do treino deve ter entre 3 e 100 caracteres.")
        String nome,

        /**
         * Descrição opcional do treino.
         * - O tamanho máximo é de 500 caracteres.
         */
        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
        String descricao,

        /**
         * Identificador único do Aluno.
         * - Não pode ser nulo.
         */
        @NotNull(message = "O ID do aluno é obrigatório.")
        Long alunoId,

        /**
         * Lista de DTOs para criação dos exercícios do treino.
         * A anotação @Valid garante que as validações dentro de ExercicioCreateDTO sejam acionadas.
         */
        @Valid
        List<ExercicioCreateDTO> exercicios
) {
    /**
     * Construtor compacto para garantir a inicialização da lista de exercícios.
     * Se a lista de exercícios recebida for nula, ela é substituída por uma
     * lista vazia para evitar NullPointerExceptions.
     */
    public TreinoCreateDTO {
        if (exercicios == null) {
            exercicios = new ArrayList<>();
        }
    }
}
