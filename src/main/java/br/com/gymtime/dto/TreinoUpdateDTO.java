package br.com.gymtime.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public record TreinoUpdateDTO(
        @Size(min = 3, max = 100, message = "O nome do treino deve ter entre 3 e 100 caracteres.")
        String nome,

        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
        String descricao,

        @Valid
        List<ExercicioCreateDTO> exercicios // Para atualização, tratamos como uma nova lista de exercícios
) {
    // Construtor canônico
    public TreinoUpdateDTO {
        if (exercicios == null) {
            exercicios = new ArrayList<>();
        }
    }
}