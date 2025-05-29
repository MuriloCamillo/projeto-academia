package br.com.gymtime.service;

import br.com.gymtime.dto.TreinoDTO;
import java.util.List;
import java.util.Optional;

public interface TreinoService {
    TreinoDTO createTreino(TreinoDTO.TreinoCreateDTO treinoCreateDTO);
    List<TreinoDTO> getTreinosByAlunoId(Long alunoId);
    Optional<TreinoDTO> getTreinoById(Long id);
    TreinoDTO updateTreino(Long id, TreinoDTO.TreinoUpdateDTO treinoUpdateDTO);
    void deleteTreino(Long id);
}