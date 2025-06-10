package br.com.gymtime.service;

import br.com.gymtime.dto.AlunoCreateDTO;
import br.com.gymtime.dto.AlunoResponseDTO;
import br.com.gymtime.dto.AlunoUpdateDTO;

import java.util.List;
import java.util.Optional;

public interface AlunoService {
    AlunoResponseDTO createAluno(AlunoCreateDTO alunoCreateDTO);
    List<AlunoResponseDTO> getAllAlunos();
    Optional<AlunoResponseDTO> getAlunoById(Long id);
    Optional<AlunoResponseDTO> getAlunoByEmail(String email); // Se ainda for usado
    AlunoResponseDTO updateAluno(Long id, AlunoUpdateDTO alunoUpdateDTO);
    void deleteAluno(Long id);
}