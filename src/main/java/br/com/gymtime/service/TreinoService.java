package br.com.gymtime.service;

import br.com.gymtime.dto.TreinoCreateDTO;
import br.com.gymtime.dto.TreinoResponseDTO;
import br.com.gymtime.dto.TreinoUpdateDTO;
import java.util.List;
import java.util.Optional;

public interface TreinoService {
    TreinoResponseDTO createTreino(TreinoCreateDTO treinoCreateDTO);
    List<TreinoResponseDTO> getTreinosByAlunoId(Long alunoId);
    Optional<TreinoResponseDTO> getTreinoById(Long id);
    Optional<TreinoResponseDTO> getTreinoByIdAndAlunoId(Long treinoId, Long alunoId);
    TreinoResponseDTO updateTreino(Long id, TreinoUpdateDTO treinoUpdateDTO);
    void deleteTreino(Long id);
}