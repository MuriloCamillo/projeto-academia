package br.com.gymtime.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO (Data Transfer Object) para encapsular os dados de atualização de um Treino.
 * Este record é utilizado para receber os dados via requisição REST. Todos os campos
 * são opcionais. Na atualização, a lista de exercícios fornecida substitui
 * completamente a lista de exercícios existente no treino.
 *
 * @param nome       O novo nome para o treino. Se fornecido, deve ter entre 3 e 100 caracteres.
 * @param descricao  A nova descrição para o treino. Se fornecida, o tamanho máximo é de 500 caracteres.
 * @param exercicios A nova lista de exercícios para o treino. Se fornecida, substituirá a anterior.
 */
public record TreinoUpdateDTO(
        /**
         * Novo nome para o treino.
         * Se um valor for fornecido, ele deve ter entre 3 e 100 caracteres.
         */
        @Size(min = 3, max = 100, message = "O nome do treino deve ter entre 3 e 100 caracteres.")
        String nome,

        /**
         * Nova descrição para o treino.
         * Se um valor for fornecido, o tamanho máximo é de 500 caracteres.
         */
        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
        String descricao,

        /**
         * A lista completa e atualizada de exercícios para o treino.
         * Ao processar a atualização, a lista de exercícios existente será
         * totalmente substituída por esta. A anotação @Valid garante que
         * cada exercício na lista seja validado.
         */
        @Valid
        List<ExercicioCreateDTO> exercicios
) {
    /**
     * Construtor compacto para garantir a inicialização da lista de exercícios.
     * Se a lista de exercícios recebida for nula, ela é substituída por uma
     * lista vazia para evitar NullPointerExceptions.
     */
    public TreinoUpdateDTO {
        if (exercicios == null) {
            exercicios = new ArrayList<>();
        }
    }
}
