package br.com.gymtime.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AlunoUpdateDTO(
        @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
        String nome,

        @Email(message = "Formato de email inválido.")
        String email,

        @Pattern(regexp = "^[0-9]{10,11}$|^$", message = "Telefone deve conter 10 ou 11 números, ou ser deixado em branco.")
        String telefone,

        @Pattern(regexp = "^[0-9]{11}$", message = "CPF deve conter exatamente 11 números.")
        String cpf
) {
}