package br.com.gymtime.service.impl;

import br.com.gymtime.dto.TreinoDTO;
import br.com.gymtime.exception.ResourceNotFoundException;
import br.com.gymtime.model.Aluno;
import br.com.gymtime.model.Treino;
import br.com.gymtime.repository.AlunoRepository;
import br.com.gymtime.repository.TreinoRepository;
import br.com.gymtime.service.TreinoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TreinoServiceImpl implements TreinoService {

    private final TreinoRepository treinoRepository;
    private final AlunoRepository alunoRepository; // Necessário para associar o treino a um aluno

    @Autowired
    public TreinoServiceImpl(TreinoRepository treinoRepository, AlunoRepository alunoRepository) {
        this.treinoRepository = treinoRepository;
        this.alunoRepository = alunoRepository;
    }

    // Método de conversão Entidade para DTO
    private TreinoDTO convertToTreinoDTO(Treino treino) {
        if (treino == null) return null;
        return new TreinoDTO(
                treino.getId(),
                treino.getNome(),
                treino.getDescricao(),
                treino.getDataCriacao(),
                treino.getDataAtualizacao(),
                treino.getAluno() != null ? treino.getAluno().getId() : null
        );
    }

    @Transactional
    @Override
    public TreinoDTO createTreino(TreinoDTO.TreinoCreateDTO treinoCreateDTO) {
        Aluno aluno = alunoRepository.findById(treinoCreateDTO.alunoId())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com ID: " + treinoCreateDTO.alunoId() + " para associar ao treino."));

        Treino treino = new Treino();
        // Copia nome e descrição do DTO. Não copia alunoId diretamente para o campo aluno.
        treino.setNome(treinoCreateDTO.nome());
        treino.setDescricao(treinoCreateDTO.descricao());
        treino.setAluno(aluno); // Associa o aluno encontrado

        Treino savedTreino = treinoRepository.save(treino);
        return convertToTreinoDTO(savedTreino);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TreinoDTO> getTreinosByAlunoId(Long alunoId) {
        if (!alunoRepository.existsById(alunoId)) {
            throw new ResourceNotFoundException("Aluno não encontrado com ID: " + alunoId);
        }
        return treinoRepository.findByAlunoId(alunoId).stream()
                .map(this::convertToTreinoDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<TreinoDTO> getTreinoById(Long id) {
        return treinoRepository.findById(id)
                .map(this::convertToTreinoDTO);
    }

    @Transactional
    @Override
    public TreinoDTO updateTreino(Long id, TreinoDTO.TreinoUpdateDTO treinoUpdateDTO) {
        Treino treino = treinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado com ID: " + id));

        // Atualiza apenas os campos não nulos do DTO
        if (treinoUpdateDTO.nome() != null) {
            treino.setNome(treinoUpdateDTO.nome());
        }
        if (treinoUpdateDTO.descricao() != null) {
            treino.setDescricao(treinoUpdateDTO.descricao());
        }
        // Geralmente não se muda o aluno de um treino existente, mas se fosse necessário,
        // a lógica para buscar o novo aluno e associá-lo seria aqui.

        Treino updatedTreino = treinoRepository.save(treino);
        return convertToTreinoDTO(updatedTreino);
    }

    @Transactional
    @Override
    public void deleteTreino(Long id) {
        if (!treinoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Treino não encontrado com ID: " + id);
        }
        treinoRepository.deleteById(id);
    }
}