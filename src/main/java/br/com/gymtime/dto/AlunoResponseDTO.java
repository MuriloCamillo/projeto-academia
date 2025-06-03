package br.com.gymtime.dto;

import java.util.List;

public record AlunoResponseDTO(
        Long id,
        String nome,
        String email,
        String telefone, // Armazena apenas dígitos
        String cpf,      // Armazena apenas dígitos
        List<TreinoResponseDTO> treinos
) {
    // Método para formatar o CPF para exibição
    public String getFormattedCpf() {
        if (cpf != null && cpf.matches("\\d{11}")) {
            return cpf.substring(0, 3) + "." +
                    cpf.substring(3, 6) + "." +
                    cpf.substring(6, 9) + "-" +
                    cpf.substring(9, 11);
        }
        return cpf; // Retorna original se não estiver no formato esperado para formatação
    }

    // Método para formatar o Telefone para exibição
    public String getFormattedTelefone() {
        if (telefone != null && !telefone.isEmpty()) {
            if (telefone.matches("\\d{11}")) { // Celular (XX) XXXXX-XXXX
                return "(" + telefone.substring(0, 2) + ") " +
                        telefone.substring(2, 7) + "-" +
                        telefone.substring(7, 11);
            } else if (telefone.matches("\\d{10}")) { // Fixo (XX) XXXX-XXXX
                return "(" + telefone.substring(0, 2) + ") " +
                        telefone.substring(2, 6) + "-" +
                        telefone.substring(6, 10);
            }
        }
        return telefone; // Retorna original ou vazio/nulo
    }
}