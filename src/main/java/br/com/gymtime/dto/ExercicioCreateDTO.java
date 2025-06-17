package br.com.gymtime.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO (Data Transfer Object) para encapsular os dados de criação de um novo Exercício.
 * Esta classe é utilizada para receber os dados de um exercício como parte da criação
 * ou atualização de um treino. É implementada como uma classe (e não um record) para
 * ser compatível com o data binding de formulários web dinâmicos que requerem um
 * construtor padrão e setters.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ExercicioCreateDTO {

        /**
         * O nome do exercício.
         * - Não pode ser nulo ou em branco.
         * - O tamanho máximo é de 150 caracteres.
         */
        @NotBlank(message = "O nome do exercício não pode estar em branco.")
        @Size(max = 150, message = "Nome do exercício muito longo (máx 150 caracteres).")
        private String nomeExercicio;

        /**
         * A descrição das séries e repetições (ex: "3x10").
         * - Campo opcional.
         * - O tamanho máximo é de 100 caracteres.
         */
        @Size(max = 100, message = "Séries/repetições muito longas (máx 100 caracteres).")
        private String seriesRepeticoes;
}
