package br.com.gymtime.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) para encapsular os dados de criação de um novo Aluno.
 * Este record é utilizado para receber os dados via requisição REST e aplicar as
 * validações necessárias antes de processar a criação do aluno na camada de serviço.
 *
 * @param nome     O nome completo do aluno. Deve conter apenas letras e espaços.
 * @param email    O endereço de e-mail do aluno. Deve ser único e ter um formato válido.
 * @param telefone O número de telefone do aluno (opcional). Deve conter 10 ou 11 dígitos.
 * @param cpf      O CPF do aluno. Deve ser único e conter exatamente 11 dígitos.
 */
public record AlunoCreateDTO(
        /**
         * Nome do aluno.
         * - Não pode ser nulo ou em branco.
         * - Deve ter entre 2 e 100 caracteres.
         * - Deve conter apenas letras e espaços.
         */
        @NotBlank(message = "O nome não pode estar em branco.")
        @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ ]+$", message = "O nome deve conter apenas letras e espaços.")
        String nome,

        /**
         * Email do aluno.
         * - Não pode ser nulo ou em branco.
         * - Deve seguir um formato de e-mail válido.
         */
        @Email(message = "Formato de email inválido.")
        @NotBlank(message = "O email não pode estar em branco.")
        String email,

        /**
         * Telefone do aluno.
         * - Campo opcional.
         * - Se preenchido, deve conter 10 ou 11 dígitos numéricos.
         * - Máscara de formatação (ex: (XX) XXXX-XXXX).
         */
        @Pattern(regexp = "^[0-9]{10,11}$|^$", message = "Telefone deve conter 10 ou 11 números, ou ser deixado em branco.")
        String telefone,

        /**
         * CPF do aluno.
         * - Não pode ser nulo ou em branco.
         * - Deve conter exatamente 11 dígitos numéricos.
         * - Máscara de formatação (ex: XXX.XXX.XXX-XX).
         */
        @NotBlank(message = "O CPF não pode estar em branco.")
        @Pattern(regexp = "^[0-9]{11}$", message = "CPF deve conter exatamente 11 números.")
        String cpf
) {
}