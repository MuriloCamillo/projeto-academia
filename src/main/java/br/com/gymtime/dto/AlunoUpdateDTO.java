package br.com.gymtime.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) para encapsular os dados de atualização de um Aluno.
 * <p>
 * Este record é utilizado para receber os dados via requisição REST. Todos os campos
 * são opcionais. O cliente deve enviar apenas os campos que deseja modificar.
 *
 * @param nome     O novo nome do aluno. Se fornecido, deve ter entre 2 e 100 caracteres.
 * @param email    O novo e-mail do aluno. Se fornecido, deve ser único e ter um formato válido.
 * @param telefone O novo telefone do aluno. Se fornecido, deve conter 10 ou 11 dígitos.
 * @param cpf      O novo CPF do aluno. Se fornecido, deve ser único e conter 11 dígitos.
 */
public record AlunoUpdateDTO(
        /**
         * Novo nome para o aluno.
         * Se um valor for fornecido, ele deve ter entre 2 e 100 caracteres.
         */
        @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
        String nome,

        /**
         * Novo email para o aluno.
         * Se um valor for fornecido, ele deve ter um formato de e-mail válido.
         */
        @Email(message = "Formato de email inválido.")
        String email,

        /**
         * Novo telefone para o aluno.
         * Se um valor for fornecido, ele deve conter 10 ou 11 dígitos, ou ser uma string vazia para remover o telefone.
         */
        @Pattern(regexp = "^[0-9]{10,11}$|^$", message = "Telefone deve conter 10 ou 11 números, ou ser deixado em branco.")
        String telefone,

        /**
         * Novo CPF para o aluno.
         * Se um valor for fornecido, ele deve conter exatamente 11 dígitos.
         */
        @Pattern(regexp = "^[0-9]{11}$", message = "CPF deve conter exatamente 11 números.")
        String cpf
) {
}