package br.com.gymtime.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public record TreinoCreateDTO(
        @NotBlank(message = "O nome do treino não pode estar em branco.")
        @Size(min = 3, max = 100, message = "O nome do treino deve ter entre 3 e 100 caracteres.")
        String nome,

        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
        String descricao,

        @NotNull(message = "O ID do aluno é obrigatório.")
        Long alunoId,

        @Valid
        List<ExercicioCreateDTO> exercicios
) {
    // Construtor canônico que garante que a lista de exercícios seja inicializada
    public TreinoCreateDTO {
        if (exercicios == null) {
            exercicios = new ArrayList<>();
        }
    }

    // Adicionar um construtor sem argumentos se o Spring também precisar para o objeto principal do formulário,
    // embora geralmente o @ModelAttribute consiga lidar com records com construtores canônicos.
    // Se você ainda tiver problemas com TreinoCreateDTO, você pode adicionar:
    // public TreinoCreateDTO() {
    //     this(null, null, null, new ArrayList<>());
    // }
}