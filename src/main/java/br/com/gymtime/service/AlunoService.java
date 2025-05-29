package br.com.gymtime.service;

import br.com.gymtime.dto.AlunoDTO;
import br.com.gymtime.model.Aluno;

import java.util.List;
import java.util.Optional;

public interface AlunoService {
    AlunoDTO.AlunoCreateDTO createAluno(AlunoDTO.AlunoCreateDTO alunoCreateDTO);
    List<AlunoDTO> getAllAlunos();
    Optional<AlunoDTO> getAlunoById(Long id);
    Optional<AlunoDTO> getAlunoByEmail(String email);
    AlunoDTO updateAluno(Long id, AlunoDTO.AlunoUpdateDTO alunoUpdateDTO);
    void deleteAluno(Long id);
}