package br.com.gymtime.dto;

import java.util.List;

/**
 * DTO (Data Transfer Object) para encapsular os dados de resposta de um Aluno.
 * Este record representa a visão de um aluno que é enviada para o cliente da API.
 * Ele contém dados formatados e prontos para exibição.
 *
 * @param id       O identificador único do aluno.
 * @param nome     O nome completo do aluno.
 * @param email    O endereço de e-mail do aluno.
 * @param telefone O número de telefone do aluno, contendo apenas dígitos.
 * @param cpf      O CPF do aluno, contendo apenas os 11 dígitos.
 * @param treinos  Uma lista de treinos associados a este aluno.
 */
public record AlunoResponseDTO(
        Long id,
        String nome,
        String email,
        String telefone,
        String cpf,
        List<TreinoResponseDTO> treinos
) {
    /**
     * Formata o CPF armazenado para o padrão de exibição brasileiro.
     * Exemplo: "12345678901" se torna "123.456.789-01".
     *
     * @return O CPF formatado como String. Se o CPF for nulo ou inválido, retorna o valor original.
     */
    public String getFormattedCpf() {
        if (cpf != null && cpf.matches("\\d{11}")) { //
            return cpf.substring(0, 3) + "." +
                    cpf.substring(3, 6) + "." +
                    cpf.substring(6, 9) + "-" +
                    cpf.substring(9, 11);
        }
        return cpf; // Retorna original se não estiver no formato esperado para formatação
    }

    /**
     * Formata o número de telefone armazenado para um padrão de exibição.
     * Cobre formatos de celular (11 dígitos) e fixo (10 dígitos).
     * Exemplo: "99999999999" se torna "(99) 99999-9999".
     * Exemplo: "9999999999" se torna "(99) 9999-9999".
     *
     * @return O telefone formatado como String. Se o telefone for nulo, vazio ou inválido, retorna o valor original.
     */
    public String getFormattedTelefone() {
        if (telefone != null && !telefone.isEmpty()) { //
            if (telefone.matches("\\d{11}")) { // Celular (XX) XXXXX-XXXX //
                return "(" + telefone.substring(0, 2) + ") " +
                        telefone.substring(2, 7) + "-" +
                        telefone.substring(7, 11);
            } else if (telefone.matches("\\d{10}")) { // Fixo (XX) XXXX-XXXX //
                return "(" + telefone.substring(0, 2) + ") " +
                        telefone.substring(2, 6) + "-" +
                        telefone.substring(6, 10);
            }
        }
        return telefone; // Retorna original ou vazio/nulo
    }
}