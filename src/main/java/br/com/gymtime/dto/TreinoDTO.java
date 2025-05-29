package br.com.gymtime.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TreinoDTO(
        Long id,

        @NotBlank(message = "O nome do treino não pode estar em branco.")
        @Size(min = 3, max = 100, message = "O nome do treino deve ter entre 3 e 100 caracteres.")
        String nome,

        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
        String descricao,

        LocalDate dataCriacao,
        LocalDate dataAtualizacao,
        Long alunoId // Para referência, mas sem o objeto Aluno completo para evitar ciclos
) {
    public record TreinoCreateDTO(
            @NotBlank(message = "O nome do treino não pode estar em branco.")
            @Size(min = 3, max = 100, message = "O nome do treino deve ter entre 3 e 100 caracteres.")
            String nome,

            @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
            String descricao,

            @NotNull(message = "O ID do aluno é obrigatório.")
            Long alunoId
    ) {}

    public record TreinoUpdateDTO(
            @Size(min = 3, max = 100, message = "O nome do treino deve ter entre 3 e 100 caracteres.")
            String nome,

            @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
            String descricao
            // alunoId geralmente não é atualizado, o treino continua do mesmo aluno
    ) {}
}