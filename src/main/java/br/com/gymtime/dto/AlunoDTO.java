package br.com.gymtime.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record AlunoDTO(
        Long id,

        @NotBlank(message = "O nome não pode estar em branco.")
        @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
        String nome,

        @Email(message = "Formato de email inválido.")
        @NotBlank(message = "O email não pode estar em branco.")
        String email,

        @Size(max = 15, message = "O telefone deve ter no máximo 15 caracteres.")
        String telefone,

        List<TreinoDTO> treinos // Pode ser útil para exibir treinos ao buscar um aluno
) {
    // Se precisar de um DTO para criação sem o ID e sem a lista de treinos:
    public record AlunoCreateDTO(
            @NotBlank(message = "O nome não pode estar em branco.")
            @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
            String nome,

            @Email(message = "Formato de email inválido.")
            @NotBlank(message = "O email não pode estar em branco.")
            String email,

            @Size(max = 15, message = "O telefone deve ter no máximo 15 caracteres.")
            String telefone
    ) {}

    public record AlunoUpdateDTO(
            @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
            String nome, // Campos opcionais para atualização

            @Email(message = "Formato de email inválido.")
            String email,

            @Size(max = 15, message = "O telefone deve ter no máximo 15 caracteres.")
            String telefone
    ) {}
}